package com.shimady563.contest.manager.service.impl;

import com.shimady563.contest.manager.converter.GroupConverter;
import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.dto.GroupRequestDto;
import com.shimady563.contest.manager.model.dto.GroupResponseDto;
import com.shimady563.contest.manager.repository.GroupRepository;
import com.shimady563.contest.manager.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class GroupServiceImpl extends InternalGroupService implements GroupService {
    public GroupServiceImpl(GroupRepository groupRepository) {
        super(groupRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponseDto> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(GroupConverter::domain2Response)
                .toList();
    }

    @Override
    @Transactional
    public void createGroup(GroupRequestDto request) {
        log.info("Creating group with name: {}", request.getName());
        groupRepository.save(GroupConverter.request2Domain(request));
    }

    @Override
    @Transactional
    public void updateGroupById(Long id, GroupRequestDto request) {
        log.info("Updating group with id: {}", id);
        Group existing = getGroupById(id);
        existing.setName(request.getName());
        groupRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteGroupById(Long id) {
        log.info("Deleting group with id: {}", id);
        groupRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupResponseDto> getGroupsByName(String name, Pageable pageRequest) {
        String query = name == null ? "" : name;
        log.info("Getting group by name: {}", query);
        return groupRepository.findByNameContainingIgnoreCase(query, pageRequest)
                .map(GroupConverter::domain2Response);
    }
}
