package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.exception.ResourceNotFoundException;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    protected Task getTaskById(Long id) {
        log.info("Getting task by id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
    }
}
