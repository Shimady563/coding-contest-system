package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.model.dto.TaskRequestDto;
import com.shimady563.contest.manager.model.dto.TaskResponseDto;
import com.shimady563.contest.manager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_TEACHER")
    public void createTask(@Valid @RequestBody TaskRequestDto request) {
        taskService.createTask(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_TEACHER")
    public void updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDto request) {
        taskService.updateTaskById(id, request);
    }

    @GetMapping("")
    @Secured({"ROLE_TEACHER"})
    public Page<TaskResponseDto> searchForTasks(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return taskService.searchForTasks(PageRequest.of(pageNumber, pageSize));
    }

    @GetMapping("/contest-version")
    @Secured({"ROLE_TEACHER", "ROLE_STUDENT"})
    public List<TaskResponseDto> getTasksByContestVersionId(@RequestParam Long contestVersionId) {
        return taskService.getTasksByContestVersionId(contestVersionId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_TEACHER")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }
}
