package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.exception.AppError;
import com.shimady563.contest.manager.exception.ValidationError;
import com.shimady563.contest.manager.model.dto.TaskRequestDto;
import com.shimady563.contest.manager.model.dto.TaskResponseDto;
import com.shimady563.contest.manager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tasks", description = "Endpoints for tasks management")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized (no/invalid token)",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class))),
        @ApiResponse(responseCode = "403", description = "Authentication or authorization error",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)))
})
public class TaskController {
    private final TaskService taskService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_TEACHER")
    @Operation(summary = "Create task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationError.class))),
    })
    public void createTask(@Valid @RequestBody TaskRequestDto request) {
        taskService.createTask(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_TEACHER")
    @Operation(summary = "Update task")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task updated"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationError.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))),
    })
    public void updateTask(@Parameter(description = "Task id") @PathVariable Long id, @Valid @RequestBody TaskRequestDto request) {
        taskService.updateTaskById(id, request);
    }

    @GetMapping("")
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Search tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of tasks")
    })
    public Page<TaskResponseDto> searchForTasks(
            @Parameter(description = "Name filter") @RequestParam String name,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return taskService.searchForTasks(name, PageRequest.of(pageNumber, pageSize));
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Get task by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public TaskResponseDto getTaskById(@Parameter(description = "Task id") @PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping("/contest-version")
    @Secured("ROLE_STUDENT")
    @Operation(summary = "Get tasks by contest version id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of tasks",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TaskResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "Contest version not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public List<TaskResponseDto> getTasksByContestVersionId(@Parameter(description = "Contest version id") @RequestParam Long contestVersionId) {
        return taskService.getTasksByContestVersionId(contestVersionId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_TEACHER")
    @Operation(summary = "Delete task by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "409", description = "Task is in one of the contests (delete them first)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public void deleteTaskById(@Parameter(description = "Task id") @PathVariable Long id) {
        taskService.deleteTaskById(id);
    }
}
