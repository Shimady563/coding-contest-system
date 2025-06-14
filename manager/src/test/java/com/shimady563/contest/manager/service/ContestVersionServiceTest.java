package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.dto.ContestVersionRequestDto;
import com.shimady563.contest.manager.model.dto.ContestVersionResponseDto;
import com.shimady563.contest.manager.repository.ContestVersionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ContestVersionServiceTest {

    @Mock
    private ContestVersionRepository contestVersionRepository;

    @Mock
    private ContestService contestService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private ContestVersionService contestVersionService;

    @Test
    void shouldGetContestVersionById() {
        Long id = 1L;
        ContestVersion contestVersion = new ContestVersion();
        contestVersion.setId(id);

        given(contestVersionRepository.findById(id)).willReturn(Optional.of(contestVersion));

        ContestVersion result = contestVersionService.getContestVersionById(id);

        assertEquals(contestVersion, result);
    }

    @Test
    void shouldThrowWhenContestVersionNotFoundById() {
        given(contestVersionRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contestVersionService.getContestVersionById(999L));
    }

    @Test
    void shouldGetContestVersionWithUsersById() {
        Long id = 1L;
        ContestVersion contestVersion = new ContestVersion();
        contestVersion.setId(id);

        given(contestVersionRepository.findByIdFetchUsers(id)).willReturn(Optional.of(contestVersion));

        ContestVersion result = contestVersionService.getContestVersionWithUsersById(id);

        assertEquals(contestVersion, result);
    }

    @Test
    void shouldThrowWhenContestVersionWithUsersNotFound() {
        given(contestVersionRepository.findByIdFetchUsers(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contestVersionService.getContestVersionWithUsersById(999L));
    }

    @Test
    void shouldCreateContestVersion() {
        Long contestId = 1L;
        ContestVersionRequestDto request = new ContestVersionRequestDto();
        request.setContestId(contestId);
        request.setName("Round 1");
        request.setTaskIds(List.of(101L, 102L));

        Contest contest = new Contest();
        Task task1 = new Task();
        task1.setId(request.getTaskIds().getFirst());
        Task task2 = new Task();
        task1.setId(request.getTaskIds().get(1));

        given(contestService.getContestById(contestId)).willReturn(contest);
        given(taskService.getTasksByIds(List.of(101L, 102L))).willReturn(List.of(task1, task2));

        contestVersionService.createContestVersion(request);

        then(contestVersionRepository).should().save(any(ContestVersion.class));
    }

    @Test
    void shouldGetContestVersionsByContestId() {
        Contest contest = new Contest();
        ContestVersion contestVersion = new ContestVersion();
        ContestVersionResponseDto dto = new ContestVersionResponseDto();
        dto.setTaskIds(List.of());

        given(contestService.getContestById(1L)).willReturn(contest);
        given(contestVersionRepository.findByContest(contest)).willReturn(List.of(contestVersion));

        List<ContestVersionResponseDto> result = contestVersionService.getContestVersionsByContestId(1L);

        assertThat(result).hasSize(1).containsExactly(dto);
    }
}
