package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.model.dto.GroupRequestDto;
import com.shimady563.contest.manager.model.dto.GroupResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupService {
    List<GroupResponseDto> getAllGroups();

    void createGroup(GroupRequestDto request);

    void updateGroupById(Long id, GroupRequestDto request);

    void deleteGroupById(Long id);

    Page<GroupResponseDto> getGroupsByName(String name, Pageable pageRequest);
}
