package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.AccessDeniedException;
import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Role;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.TestCase;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.TaskRequestDto;
import com.shimady563.contest.manager.model.dto.TaskResponseDto;
import com.shimady563.contest.manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ModelMapper mapper;
    private final UserService userService;

    protected Task getTaskById(Long id) {
        log.info("Getting task by id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
    }

    @Transactional
    public void createTask(TaskRequestDto request) {
        log.info("Creating task with name: {}, test cases: {}",
                request.getName(), request.getTestCases());
        Task task = new Task();
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        request.getTestCases()
                .stream()
                .map(dto -> mapper.map(dto, TestCase.class))
                .forEach(task::addTestCase);
        task.setTestCasesCount((short) task.getTestCases().size());
        taskRepository.save(task);
    }

    @Transactional
    public void updateTaskById(Long id, TaskRequestDto request) {
        log.info("Creating task with name: {}", request.getName());
        Task task = getTaskById(id);
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        Set<TestCase> newTestCases = request.getTestCases()
                .stream()
                .map(dto -> mapper.map(dto, TestCase.class))
                .collect(Collectors.toSet());
        task.getTestCases().retainAll(newTestCases);
        task.setTestCasesCount((short) task.getTestCases().size());
        taskRepository.save(task);
    }

    protected List<Task> getTasksByIds(List<Long> ids) {
        return taskRepository.findByIdIn(ids);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponseDto> searchForTasks(Long contestVersionId, PageRequest pageRequest) {
        log.info("Searching for tasks with contest version id: {}", contestVersionId);
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == Role.ROLE_STUDENT
                && currentUser.getContestVersions()
                .stream()
                .filter(cv -> cv.getId().equals(contestVersionId))
                .count() != 1) {
            throw new AccessDeniedException("Access to tasks of contest version with id: " + contestVersionId + " denied for user with id: " + currentUser.getId());
        }
        return taskRepository.findAll(pageRequest)
                .map(t -> mapper.map(t, TaskResponseDto.class));
    }

    @Transactional
    public void deleteTaskById(Long id) {
        log.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
    }
}
