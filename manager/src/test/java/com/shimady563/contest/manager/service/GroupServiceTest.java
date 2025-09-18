package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.dto.GroupResponseDto;
import com.shimady563.contest.manager.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @Test
    void shouldGetGroupById() {
        Long id = 1L;
        Group group = new Group();
        group.setId(id);

        given(groupRepository.findById(id)).willReturn(Optional.of(group));

        Group result = groupService.getGroupById(id);

        assertEquals(group, result);
    }

    @Test
    void shouldThrowWhenGroupNotFoundById() {
        given(groupRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> groupService.getGroupById(999L));
    }

    @Test
    void shouldGetAllGroups() {
        Group group1 = new Group();
        Group group2 = new Group();
        GroupResponseDto dto1 = new GroupResponseDto();
        GroupResponseDto dto2 = new GroupResponseDto();

        given(groupRepository.findAll()).willReturn(List.of(group1, group2));

        List<GroupResponseDto> result = groupService.getAllGroups();

        assertThat(result).hasSize(2).contains(dto1, dto2);
    }

    @Test
    void shouldGetGroupsByName() {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "Test";
        Group group1 = new Group();
        group1.setName("Test Group 1");
        Group group2 = new Group();
        group2.setName("Another Test Group");
        GroupResponseDto dto1 = new GroupResponseDto();
        dto1.setName("Test Group 1");
        GroupResponseDto dto2 = new GroupResponseDto();
        dto2.setName("Another Test Group");

        given(groupRepository.findByNameContainingIgnoreCase(name, pageable))
                .willReturn(new PageImpl<>(List.of(group1, group2)));

        Page<GroupResponseDto> result = groupService.getGroupsByName(name, pageable);

        assertThat(result.getContent()).hasSize(2).contains(dto1, dto2);
    }
}
