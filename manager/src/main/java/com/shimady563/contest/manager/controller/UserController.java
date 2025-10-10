package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.exception.AppError;
import com.shimady563.contest.manager.exception.ValidationError;
import com.shimady563.contest.manager.model.Role;
import com.shimady563.contest.manager.model.dto.UserRegistrationRequestDto;
import com.shimady563.contest.manager.model.dto.UserResponseDto;
import com.shimady563.contest.manager.model.dto.UserUpdateRequestDto;
import com.shimady563.contest.manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
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
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Get user by id", description = "Returns detailed information about a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public UserResponseDto getUserById(@Parameter(description = "User id", required = true) @PathVariable Long id) {
        return userService.getUserResponseById(id);
    }

    @GetMapping("")
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Search users", description = "Search for users with optional filters and pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of users")
    })
    public Page<UserResponseDto> searchForUsers(
            @Parameter(description = "Filter by first name") @RequestParam(required = false) String firstName,
            @Parameter(description = "Filter by last name") @RequestParam(required = false) String lastName,
            @Parameter(description = "Filter by email") @RequestParam(required = false) String email,
            @Parameter(description = "Filter by role") @RequestParam(defaultValue = "ROLE_STUDENT") Role role,
            @Parameter(description = "Filter by group name") @RequestParam(required = false) String groupName,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return userService.searchForUsers(firstName, lastName, email, role, groupName, PageRequest.of(pageNumber, pageSize));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Update user by id", description = "Updates user information")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationError.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "409", description = "User with such email already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public void updateUserById(@Parameter(description = "User id", required = true) @PathVariable Long id,
                               @Valid @RequestBody UserUpdateRequestDto request) {
        userService.updateUserById(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    @Operation(summary = "Delete user by id", description = "Deletes a user by their id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "409", description = "User tried to delete themselves",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public void deleteUserById(@Parameter(description = "User id") @PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @PatchMapping("/start/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_STUDENT", "ROLE_TEACHER"})
    @Operation(summary = "Register user for contest version", description = "Registers a user for a specific contest version")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User registered"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationError.class))),
            @ApiResponse(responseCode = "403",
                    description = "User don't have permission to register for this contest version or already registered for other contest version in specified contest",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "404", description = "User or contest not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)))
    })
    public void registerUserForContestVersion(@Parameter(description = "User id") @PathVariable Long id,
                                              @Valid @RequestBody UserRegistrationRequestDto request) {
        userService.registerUserForContestVersion(id, request);
    }
}
