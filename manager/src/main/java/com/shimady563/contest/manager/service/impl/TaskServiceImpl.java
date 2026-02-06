package com.shimady563.contest.manager.service.impl;

import com.shimady563.contest.manager.converter.TaskConverter;
import com.shimady563.contest.manager.exception.AccessDeniedException;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.TaskRequestDto;
import com.shimady563.contest.manager.model.dto.TaskResponseDto;
import com.shimady563.contest.manager.repository.TaskRepository;
import com.shimady563.contest.manager.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TaskServiceImpl extends InternalTaskService implements TaskService {
    private final UserServiceImpl userService;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            UserServiceImpl userService
    ) {
        super(taskRepository);
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = getTaskByIdInternal(id);
        return TaskConverter.domain2Response(task);
    }

    @Override
    @Transactional
    public void createTask(TaskRequestDto request) {
        log.info("Creating task with name: {}, test cases: {}",
                request.getName(), request.getTestCases());
        Task task = TaskConverter.request2Domain(request);
        request.getTestCases().stream().map(TaskConverter::dto2TestCase).forEach(task::addTestCase);
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void updateTaskById(Long id, TaskRequestDto request) {
        log.info("Updating task with name: {}", request.getName());
        Task task = getTaskByIdInternal(id);
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.getTestCases().clear();
        request.getTestCases()
                .stream()
                .map(TaskConverter::dto2TestCase)
                .forEach(task::addTestCase);
        task.setTestCasesCount((short) task.getTestCases().size());
        taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDto> searchForTasks(String name, PageRequest pageRequest) {
        log.info("Searching for tasks");
        return taskRepository.findByNameContainingIgnoreCase(name, pageRequest)
                .map(TaskConverter::domain2Response);
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        log.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByContestVersionId(Long contestVersionId) {
        log.info("Searching for tasks with contest version id: {}", contestVersionId);
        User currentUser = userService.getCurrentUser();
        Optional<ContestVersion> optContestVersion = currentUser.getContestVersions()
                .stream()
                .filter(cv -> cv.getId().equals(contestVersionId))
                .findFirst();

        if (optContestVersion.isEmpty()) {
            throw new AccessDeniedException("Access to tasks of contest version with id: " + contestVersionId + " denied for user with id: " + currentUser.getId());
        }

        ContestVersion contestVersion = optContestVersion.get();

        return taskRepository.findByContestVersions(contestVersion).stream()
                .map(TaskConverter::domain2Response)
                .toList();
    }
}
