package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.model.Role;
import com.shimady563.contest.manager.model.dto.UserResponse;
import com.shimady563.contest.manager.model.dto.UserUpdateRequest;
import com.shimady563.contest.manager.service.UserService;
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
@Secured({"ROLE_TEACHER"})
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserResponseById(id);
    }

    @GetMapping("")
    public Page<UserResponse> searchForUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "ROLE_USER") Role role,
            @RequestParam(required = false) String groupName,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return userService.searchForUsers(firstName, lastName, email, role, groupName, PageRequest.of(pageNumber, pageSize));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserById(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUserById(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}
