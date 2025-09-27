package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.config.props.KafkaTopicProperties;
import com.shimady563.contest.manager.exception.SubmissionInvalidException;
import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.CodeSubmission;
import com.shimady563.contest.manager.model.dto.CodeSubmissionDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {
    private static final KafkaTopicProperties PROPS = new KafkaTopicProperties();

    @Mock
    private UserService userService;

    @Mock
    private ContestVersionService contestVersionService;

    @Mock
    private TaskService taskService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private SubmissionService submissionService;

    @BeforeEach
    @SneakyThrows
    public void setUp() {
        var topicInfo = new KafkaTopicProperties.TopicInfo();
        topicInfo.setName("submission");
        PROPS.setSubmission(topicInfo);
        Field field = submissionService.getClass().getDeclaredField("kafkaProperties");
        field.setAccessible(true);
        field.set(submissionService, PROPS);
    }

    @Test
    void shouldSubmitValidSolution() {
        Long userId = 1L;
        Long contestVersionId = 2L;
        Long taskId = 3L;
        CodeSubmissionDto dto = new CodeSubmissionDto();
        dto.setUserId(userId);
        dto.setContestVersionId(contestVersionId);
        dto.setSubmittedAt(LocalDateTime.now());
        dto.setTaskId(taskId);

        User user = new User();
        user.setId(userId);

        Task task = new Task();
        task.setId(taskId);

        ContestVersion contestVersion = new ContestVersion();
        contestVersion.setId(contestVersionId);
        contestVersion.addTask(task);

        Contest contest = new Contest();
        contest.setStartTime(LocalDateTime.now().minusMinutes(10));
        contest.setEndTime(LocalDateTime.now().plusMinutes(10));

        contestVersion.setContest(contest);
        user.addContestVersion(contestVersion);

        CodeSubmission submission = new CodeSubmission();
        submission.setUserId(userId);
        submission.setTaskId(taskId);
        submission.setSubmittedAt(dto.getSubmittedAt());

        given(contestVersionService.getContestVersionById(contestVersionId)).willReturn(contestVersion);
        given(userService.getUserById(userId)).willReturn(user);
        given(taskService.getTaskByIdInternal(taskId)).willReturn(task);

        submissionService.submitSolution(dto);

        then(kafkaTemplate).should().send(PROPS.getSubmission().getName(), submission);
    }

    @Test
    void shouldThrowIfSubmittedBeforeStartTime() {
        Long contestVersionId = 1L;
        CodeSubmissionDto dto = new CodeSubmissionDto();
        dto.setContestVersionId(contestVersionId);
        dto.setSubmittedAt(LocalDateTime.now());

        ContestVersion contestVersion = new ContestVersion();
        contestVersion.setId(contestVersionId);

        Contest contest = new Contest();
        contest.setStartTime(LocalDateTime.now().plusMinutes(10));
        contest.setEndTime(LocalDateTime.now().plusMinutes(20));

        contestVersion.setContest(contest);

        given(contestVersionService.getContestVersionById(contestVersionId)).willReturn(contestVersion);

        assertThatThrownBy(() -> submissionService.submitSolution(dto))
                .isInstanceOf(SubmissionInvalidException.class)
                .hasMessageContaining("Submission was sent after or before");

        then(userService).shouldHaveNoInteractions();
        then(kafkaTemplate).shouldHaveNoInteractions();
        then(taskService).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowIfSubmittedAfterEndTime() {
        Long contestVersionId = 1L;
        CodeSubmissionDto dto = new CodeSubmissionDto();
        dto.setContestVersionId(contestVersionId);
        dto.setSubmittedAt(LocalDateTime.now());

        ContestVersion contestVersion = new ContestVersion();
        contestVersion.setId(contestVersionId);

        Contest contest = new Contest();
        contest.setStartTime(LocalDateTime.now().minusMinutes(20));
        contest.setEndTime(LocalDateTime.now().minusMinutes(10));

        contestVersion.setContest(contest);

        given(contestVersionService.getContestVersionById(contestVersionId)).willReturn(contestVersion);

        assertThatThrownBy(() -> submissionService.submitSolution(dto))
                .isInstanceOf(SubmissionInvalidException.class)
                .hasMessageContaining("Submission was sent after or before");

        then(userService).shouldHaveNoInteractions();
        then(kafkaTemplate).shouldHaveNoInteractions();
        then(taskService).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowIfUserNotInContestVersion() {
        Long userId = 1L;
        Long contestVersionId = 2L;
        Long taskId = 3L;
        CodeSubmissionDto dto = new CodeSubmissionDto();
        dto.setUserId(userId);
        dto.setContestVersionId(contestVersionId);
        dto.setSubmittedAt(LocalDateTime.now());
        dto.setTaskId(taskId);

        User user = new User();
        user.setId(userId);

        Task task = new Task();
        task.setId(taskId);

        ContestVersion contestVersion = new ContestVersion();
        contestVersion.setId(contestVersionId);
        contestVersion.addTask(task);

        Contest contest = new Contest();
        contest.setStartTime(LocalDateTime.now().minusMinutes(10));
        contest.setEndTime(LocalDateTime.now().plusMinutes(10));

        contestVersion.setContest(contest);

        given(contestVersionService.getContestVersionById(contestVersionId)).willReturn(contestVersion);
        given(userService.getUserById(userId)).willReturn(user);
        given(taskService.getTaskByIdInternal(taskId)).willReturn(task);

        assertThatThrownBy(() -> submissionService.submitSolution(dto))
                .isInstanceOf(SubmissionInvalidException.class)
                .hasMessageContaining("doesn't have the access to contest version");

        then(kafkaTemplate).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowIfTaskNotInContestVersion() {
        Long userId = 1L;
        Long contestVersionId = 2L;
        Long taskId = 3L;
        CodeSubmissionDto dto = new CodeSubmissionDto();
        dto.setUserId(userId);
        dto.setContestVersionId(contestVersionId);
        dto.setSubmittedAt(LocalDateTime.now());
        dto.setTaskId(taskId);

        User user = new User();
        user.setId(userId);

        Task task = new Task();
        task.setId(taskId);

        ContestVersion contestVersion = new ContestVersion();
        contestVersion.setId(contestVersionId);

        Contest contest = new Contest();
        contest.setStartTime(LocalDateTime.now().minusMinutes(10));
        contest.setEndTime(LocalDateTime.now().plusMinutes(10));

        contestVersion.setContest(contest);

        user.addContestVersion(contestVersion);

        given(contestVersionService.getContestVersionById(contestVersionId)).willReturn(contestVersion);
        given(userService.getUserById(userId)).willReturn(user);
        given(taskService.getTaskByIdInternal(taskId)).willReturn(task);

        assertThatThrownBy(() -> submissionService.submitSolution(dto))
                .isInstanceOf(SubmissionInvalidException.class)
                .hasMessageContaining("not found in contest version");

        then(kafkaTemplate).shouldHaveNoInteractions();
    }
}
