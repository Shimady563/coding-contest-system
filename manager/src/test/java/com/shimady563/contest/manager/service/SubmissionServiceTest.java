package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.SubmissionInvalidException;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.CodeSubmission;
import com.shimady563.contest.manager.model.dto.CodeSubmissionDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private ModelMapper mapper;

    @Mock
    private UserService userService;

    @Mock
    private ContestVersionService contestVersionService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private SubmissionService submissionService;

    @Test
    void shouldSubmitValidSolution() {
        Long userId = 1L;
        Long contestVersionId = 2L;
        CodeSubmissionDto dto = new CodeSubmissionDto();
        dto.setUserId(userId);
        dto.setContestVersionId(contestVersionId);
        dto.setSubmittedAt(LocalDateTime.now());
        dto.setStartTime(LocalDateTime.now().minusMinutes(10));
        dto.setEndTime(LocalDateTime.now().plusMinutes(10));

        User user = new User();
        user.setId(userId);

        ContestVersion contestVersion = new ContestVersion();
        contestVersion.setId(contestVersionId);
        contestVersion.setUsers(Set.of(user));

        CodeSubmission submission = new CodeSubmission();

        given(contestVersionService.getContestVersionById(contestVersionId)).willReturn(contestVersion);
        given(userService.getUserById(userId)).willReturn(user);
        given(mapper.map(dto, CodeSubmission.class)).willReturn(submission);

        submissionService.submitSolution(dto);

        then(kafkaTemplate).should().send("submissionTopic", submission);
    }

    @Test
    void shouldThrowIfSubmittedBeforeStartTime() {
        CodeSubmissionDto dto = new CodeSubmissionDto();
        dto.setSubmittedAt(LocalDateTime.now().minusMinutes(15));
        dto.setStartTime(LocalDateTime.now().minusMinutes(10));
        dto.setEndTime(LocalDateTime.now().plusMinutes(10));

        assertThatThrownBy(() -> submissionService.submitSolution(dto))
                .isInstanceOf(SubmissionInvalidException.class)
                .hasMessageContaining("Submission was sent after or before");

        then(contestVersionService).shouldHaveNoInteractions();
        then(userService).shouldHaveNoInteractions();
        then(kafkaTemplate).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowIfSubmittedAfterEndTime() {
        CodeSubmissionDto dto = new CodeSubmissionDto();
        dto.setSubmittedAt(LocalDateTime.now().plusMinutes(15));
        dto.setStartTime(LocalDateTime.now().minusMinutes(10));
        dto.setEndTime(LocalDateTime.now().plusMinutes(5));

        assertThatThrownBy(() -> submissionService.submitSolution(dto))
                .isInstanceOf(SubmissionInvalidException.class)
                .hasMessageContaining("Submission was sent after or before");

        then(contestVersionService).shouldHaveNoInteractions();
        then(userService).shouldHaveNoInteractions();
        then(kafkaTemplate).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowIfUserNotInContestVersion() {
        Long userId = 1L;
        Long contestVersionId = 2L;
        CodeSubmissionDto dto = new CodeSubmissionDto();
        dto.setUserId(userId);
        dto.setContestVersionId(contestVersionId);
        dto.setSubmittedAt(LocalDateTime.now());
        dto.setStartTime(LocalDateTime.now().minusMinutes(10));
        dto.setEndTime(LocalDateTime.now().plusMinutes(10));

        User user = new User();
        user.setId(userId);

        ContestVersion contestVersion = new ContestVersion();
        contestVersion.setId(contestVersionId);

        given(contestVersionService.getContestVersionById(contestVersionId)).willReturn(contestVersion);
        given(userService.getUserById(userId)).willReturn(user);

        assertThatThrownBy(() -> submissionService.submitSolution(dto))
                .isInstanceOf(SubmissionInvalidException.class)
                .hasMessageContaining("User doesn't have the access");

        then(mapper).shouldHaveNoInteractions();
        then(kafkaTemplate).shouldHaveNoInteractions();
    }
}
