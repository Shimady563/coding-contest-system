package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.model.dto.GroupRequestDto;
import com.shimady563.contest.manager.model.dto.GroupResponseDto;
import com.shimady563.contest.manager.service.GroupService;
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
public class GroupController {
    private final GroupService groupService;

    @GetMapping("")
    public List<GroupResponseDto> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/page")
    @Secured({"ROLE_TEACHER"})
    public Page<GroupResponseDto> getGroupsPage(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return groupService.getGroupsByName(name, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({"ROLE_TEACHER"})
    public void createGroup(@Valid @RequestBody GroupRequestDto request) {
        groupService.createGroup(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    public void updateGroupById(@PathVariable Long id, @Valid @RequestBody GroupRequestDto request) {
        groupService.updateGroupById(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    public void deleteGroupById(@PathVariable Long id) {
        groupService.deleteGroupById(id);
    }
}
