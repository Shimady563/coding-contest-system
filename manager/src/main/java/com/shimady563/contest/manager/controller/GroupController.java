package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.model.dto.GroupResponseDto;
import com.shimady563.contest.manager.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
