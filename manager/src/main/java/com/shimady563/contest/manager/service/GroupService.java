package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.converter.GroupConverter;
import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.dto.GroupRequestDto;
import com.shimady563.contest.manager.model.dto.GroupResponseDto;
import com.shimady563.contest.manager.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    protected Group getGroupById(Long id) {
        log.info("Getting group by id: {}", id);
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group with id: " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<GroupResponseDto> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(GroupConverter::domain2Response)
                .toList();
    }

    @Transactional
    public void createGroup(GroupRequestDto request) {
        log.info("Creating group with name: {}", request.getName());
        groupRepository.save(GroupConverter.request2Domain(request));
    }

    @Transactional
    public void updateUserById(Long id, GroupRequestDto request) {
        log.info("Updating group with id: {}", id);
        Group existing = getGroupById(id);
        existing.setName(request.getName());
        groupRepository.save(existing);
    }

    @Transactional
    public void deleteGroupById(Long id) {
        log.info("Deleting group with id: {}", id);
        groupRepository.deleteById(id);
    }
}
