package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.exception.AppError;
import com.shimady563.contest.manager.exception.ValidationError;
import com.shimady563.contest.manager.model.dto.ContestVersionRequestDto;
import com.shimady563.contest.manager.model.dto.ContestVersionResponseDto;
import com.shimady563.contest.manager.service.ContestVersionService;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contest-versions")
@RequiredArgsConstructor
@Tag(name = "Contest Versions", description = "Manage contest versions")
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
public class ContestVersionController {
    private final ContestVersionService contestVersionService;

    @GetMapping("")
    @Secured({"ROLE_STUDENT", "ROLE_TEACHER"})
    @Operation(summary = "Get contest versions by contest id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of contest versions",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ContestVersionResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "Contest not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public List<ContestVersionResponseDto> getContestVersionsByContestId(@Parameter(description = "Contest id") @RequestParam Long contestId) {
        return contestVersionService.getContestVersionsByContestId(contestId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Create contest version")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contest version created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationError.class))),
            @ApiResponse(responseCode = "404", description = "Contest or task not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))),
    })
    public void createContestVersion(@Valid @RequestBody ContestVersionRequestDto request) {
        contestVersionService.createContestVersion(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Delete contest version by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contest version deleted"),
            @ApiResponse(responseCode = "404", description = "Contest version not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public void deleteContestVersionById(@Parameter(description = "Contest version id") @PathVariable Long id) {
        contestVersionService.deleteContestVersionById(id);
    }
}
