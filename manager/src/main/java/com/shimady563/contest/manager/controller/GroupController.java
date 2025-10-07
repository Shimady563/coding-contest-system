package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.exception.AppError;
import com.shimady563.contest.manager.exception.ValidationError;
import com.shimady563.contest.manager.model.dto.GroupRequestDto;
import com.shimady563.contest.manager.model.dto.GroupResponseDto;
import com.shimady563.contest.manager.service.GroupService;
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
@RequestMapping("/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "CRUD endpoints for student groups")
@ApiResponse(responseCode = "500", description = "Internal server error",
        content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AppError.class)))
public class GroupController {
    private final GroupService groupService;

    @GetMapping("")
    @Operation(summary = "Get all groups", description = "Returns list of all groups")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of groups",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GroupResponseDto.class))))
    })
    public List<GroupResponseDto> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/page")
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Get groups page", description = "Search groups by optional name with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of groups"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (no/invalid token)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
    })
    public Page<GroupResponseDto> getGroupsPage(
            @Parameter(description = "Optional name filter") @RequestParam(required = false) String name,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return groupService.getGroupsByName(name, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Create group", description = "Creates a new group")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Group created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized (no/invalid token)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "409", description = "Conflict (duplicate)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public void createGroup(@Valid @RequestBody GroupRequestDto request) {
        groupService.createGroup(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Update group", description = "Updates group by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Group updated"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized (no/invalid token)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "409", description = "Conflict (duplicate)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public void updateGroupById(@Parameter(description = "Group id") @PathVariable Long id, @Valid @RequestBody GroupRequestDto request) {
        groupService.updateGroupById(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Delete group", description = "Deletes group by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Group deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (no/invalid token)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public void deleteGroupById(@Parameter(description = "Group id") @PathVariable Long id) {
        groupService.deleteGroupById(id);
    }
}
