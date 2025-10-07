package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.exception.AppError;
import com.shimady563.contest.manager.model.Status;
import com.shimady563.contest.manager.model.dto.SolutionResponseDto;
import com.shimady563.contest.manager.service.SolutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/solutions")
@RequiredArgsConstructor
@Tag(name = "Solutions", description = "Endpoints for managing solutions")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized (no/invalid token)",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)))
})
public class SolutionController {
    private final SolutionService solutionService;

    @GetMapping("")
    @Secured({"ROLE_TEACHER", "ROLE_STUDENT"})
    @Operation(summary = "Search for solutions with optional filters and pagination",
            description = "Allows filtering by status, userId, taskId, and submission time range. Supports pagination.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of solutions matching the criteria",
                    content = @Content(mediaType = "application/json"))
    })
    public Page<SolutionResponseDto> searchForSolutions(
            @Parameter(description = "Optional status filter") @RequestParam(required = false) Status status,
            @Parameter(description = "Optional user id filter") @RequestParam(required = false) Long userId,
            @Parameter(description = "Optional task id filter") @RequestParam(required = false) Long taskId,
            @Parameter(description = "Optional start date filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "Optional end date filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return solutionService.searchForSolutions(status, userId, taskId, startTime, endTime, PageRequest.of(pageNumber, pageSize));
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_TEACHER", "ROLE_STUDENT"})
    @Operation(summary = "Get solution by id", description = "Returns detailed information about a specific solution")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solution found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SolutionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Solution not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public SolutionResponseDto getSolutionById(@Parameter(description = "Solution id") @PathVariable Long id) {
        return solutionService.getSolutionById(id);
    }

    @GetMapping("/task")
    @Secured({"ROLE_TEACHER", "ROLE_STUDENT"})
    @Operation(summary = "Get solution by id", description = "Returns all solutions for a specific task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solutions for specified task",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SolutionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public List<SolutionResponseDto> getSolutionsByTaskId(@Parameter(description = "Required task id filter") @RequestParam Long taskId) {
        return solutionService.getSolutionsByTaskId(taskId);
    }
}
