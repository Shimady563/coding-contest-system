package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.model.dto.TaskRequestDto;
import com.shimady563.contest.manager.model.dto.TaskResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface TaskService {
    TaskResponseDto getTaskById(Long id);

    void createTask(TaskRequestDto request);

    void updateTaskById(Long id, TaskRequestDto request);

    Page<TaskResponseDto> searchForTasks(String name, PageRequest pageRequest);

    void deleteTaskById(Long id);

    List<TaskResponseDto> getTasksByContestVersionId(Long contestVersionId);
}
