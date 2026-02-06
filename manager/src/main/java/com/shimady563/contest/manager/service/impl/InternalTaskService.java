package com.shimady563.contest.manager.service.impl;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class InternalTaskService {
    protected final TaskRepository taskRepository;

    protected Task getTaskByIdInternal(Long id) {
        log.info("Getting task by id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));
    }

    protected List<Task> getTasksByIds(List<Long> ids) {
        return taskRepository.findByIdIn(ids);
    }

}
