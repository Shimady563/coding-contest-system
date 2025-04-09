package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.TestCase;
import com.shimady563.contest.manager.model.dto.TaskRequestDto;
import com.shimady563.contest.manager.model.dto.TaskResponseDto;
import com.shimady563.contest.manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ContestVersionService contestVersionService;
    private final ModelMapper mapper;

    protected Task getTaskById(Long id) {
        log.info("Getting task by id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
    }

    @Transactional
    public void createTask(TaskRequestDto request) {
        log.info("Creating task with name: {}, contest version id: {}",
                request.getName(), request.getContestVersionId());
        ContestVersion contestVersion = null;
        if (request.getContestVersionId() != null) {
            contestVersion = contestVersionService.getContestVersionById(request.getContestVersionId());
        }

        Task task = new Task();
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setContestVersion(contestVersion);
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

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByContestVersionId(Long contestVersionId) {
        log.info("Getting tasks by contest version id: {}", contestVersionId);
        ContestVersion contestVersion = contestVersionService.getContestVersionById(contestVersionId);
        return taskRepository.findByContestVersion(contestVersion)
                .stream()
                .map(t -> mapper.map(t, TaskResponseDto.class))
                .toList();
    }
}
