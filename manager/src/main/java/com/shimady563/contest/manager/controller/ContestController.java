package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.exception.AppError;
import com.shimady563.contest.manager.model.dto.ContestRequestDto;
import com.shimady563.contest.manager.model.dto.ContestResponseDto;
import com.shimady563.contest.manager.service.ContestService;
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
@RequestMapping("/contests")
@RequiredArgsConstructor
@Tag(name = "Contests", description = "CRUD and search endpoints for contests")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized (no/invalid token)",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class)))
})
public class ContestController {
    private final ContestService contestService;

    @GetMapping("/{id}")
    @Secured({"ROLE_TEACHER", "ROLE_STUDENT"})
    @Operation(summary = "Get contest by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contest found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContestResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Contest not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public ContestResponseDto getContestById(@Parameter(description = "Contest id") @PathVariable Long id) {
        return contestService.getContestById(id);
    }

    @GetMapping("")
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Search contests by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of contests")
    })
    public Page<ContestResponseDto> getContestsByName(
            @Parameter(description = "Name filter") @RequestParam String name,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return contestService.getContestsByName(name, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Create contest")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contest created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContestResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.shimady563.contest.manager.exception.ValidationError.class))),
            @ApiResponse(responseCode = "409", description = "Conflict (duplicate)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public ContestResponseDto createContest(@Valid @RequestBody ContestRequestDto request) {
        return contestService.createContest(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Update contest")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contest updated"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.shimady563.contest.manager.exception.ValidationError.class))),
            @ApiResponse(responseCode = "404", description = "Contest not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "409", description = "Conflict (duplicate)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public void updateContest(@Parameter(description = "Contest id") @PathVariable Long id, @Valid @RequestBody ContestRequestDto request) {
        contestService.updateContestById(id, request);
    }

    @GetMapping("/group")
    @Secured({"ROLE_TEACHER", "ROLE_STUDENT"})
    @Operation(summary = "Get contests by group id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of contests",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ContestResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public List<ContestResponseDto> getContestsByGroupId(@Parameter(description = "Group id") @RequestParam Long groupId) {
        return contestService.getContestsByGroupId(groupId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Delete contest by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contest deleted"),
            @ApiResponse(responseCode = "404", description = "Contest not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public void deleteContestById(@Parameter(description = "Contest id") @PathVariable Long id) {
        contestService.deleteContestById(id);
    }
}
