package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.AccessDeniedException;
import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.TestCase;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.TaskRequestDto;
import com.shimady563.contest.manager.model.dto.TaskResponseDto;
import com.shimady563.contest.manager.model.dto.TestCaseRequestDto;
import com.shimady563.contest.manager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ModelMapper mapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequestDto requestDto;

    @BeforeEach
    void setUp() {
        TestCase testCase = new TestCase();
        testCase.setInput("in");
        testCase.setOutput("out");

        TestCaseRequestDto testCaseRequestDto = new TestCaseRequestDto();
        testCaseRequestDto.setInput("in");
        testCaseRequestDto.setOutput("out");

        task = new Task();
        task.setId(1L);
        task.setName("Sample Task");
        task.setTestCases(new HashSet<>(List.of(testCase)));

        requestDto = new TaskRequestDto();
        requestDto.setName("Sample Task");
        requestDto.setDescription("Description");
        requestDto.setTestCases(List.of(testCaseRequestDto));
    }

    @Test
    void shouldCreateTask() {
        taskService.createTask(requestDto);

        then(taskRepository).should().save(any(Task.class));
    }

    @Test
    void shouldUpdateExistingTask() {
        given(taskRepository.findById(1L)).willReturn(Optional.of(task));

        taskService.updateTaskById(1L, requestDto);

        then(taskRepository).should().save(task);
    }

    @Test
    void shouldThrowWhenTaskNotFoundOnUpdate() {
        given(taskRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTaskById(999L, requestDto));
    }

    @Test
    void shouldReturnTaskById() {
        given(taskRepository.findById(1L)).willReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);

        assertEquals(task, result);
    }

    @Test
    void shouldThrowIfTaskNotFoundById() {
        given(taskRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTaskById(1L, requestDto));
    }

    @Test
    void shouldGetTasksByIds() {
        List<Long> ids = List.of(1L, 2L);
        List<Task> tasks = List.of(new Task(), new Task());

        given(taskRepository.findByIdIn(ids)).willReturn(tasks);

        List<Task> result = taskService.getTasksByIds(ids);

        assertThat(result).hasSize(2).containsAll(tasks);
    }

    @Test
    void shouldSearchForTasks() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Task task = new Task();
        TaskResponseDto dto = new TaskResponseDto();
        dto.setTestCases(List.of());

        Page<Task> page = new PageImpl<>(List.of(task));

        given(taskRepository.findAll(pageRequest)).willReturn(page);

        Page<TaskResponseDto> result = taskService.searchForTasks(pageRequest);

        assertThat(result.getContent()).hasSize(1).containsExactly(dto);
    }

    @Test
    void shouldReturnTasksByContestVersionId() {
        Long contestVersionId = 10L;
        ContestVersion contestVersion = new ContestVersion();
        contestVersion.setId(contestVersionId);

        User user = new User();
        user.setContestVersions(Set.of(contestVersion));

        Task task = new Task();
        TaskResponseDto dto = new TaskResponseDto();
        dto.setTestCases(List.of());

        given(userService.getCurrentUser()).willReturn(user);
        given(taskRepository.findByContestVersions(contestVersion)).willReturn(List.of(task));

        List<TaskResponseDto> result = taskService.getTasksByContestVersionId(contestVersionId);

        assertThat(result).hasSize(1).containsExactly(dto);
    }

    @Test
    void shouldThrowAccessDeniedIfUserDoesNotHaveContestVersion() {
        ContestVersion contestVersion = new ContestVersion();
        contestVersion.setId(99L);


        User user = new User();
        user.setId(42L);
        user.setContestVersions(Set.of(contestVersion));

        given(userService.getCurrentUser()).willReturn(user);

        assertThatThrownBy(() -> taskService.getTasksByContestVersionId(10L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("denied");

        then(taskRepository).shouldHaveNoInteractions();
    }
}
