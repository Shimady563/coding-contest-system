package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.exception.AppError;
import com.shimady563.contest.manager.exception.ValidationError;
import com.shimady563.contest.manager.model.dto.CodeSubmissionDto;
import com.shimady563.contest.manager.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
@Secured({"ROLE_STUDENT", "ROLE_TEACHER"})
@Tag(name = "Submissions", description = "Endpoints for code submissions")
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
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Submit a solution for testing",
            description = "Accepts code submissions and initiates the testing process")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Submission accepted for processing"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationError.class))),
            @ApiResponse(responseCode = "404", description = "Task, user or contest version not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public void submitSolution(@Valid @RequestBody CodeSubmissionDto submission) {
        submissionService.submitSolution(submission);
    }
}
