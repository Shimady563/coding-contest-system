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
import org.modelmapper.ModelMapper;

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
    void shouldReturnAllGroups() {
        Group group1 = new Group();
        Group group2 = new Group();
        GroupResponseDto dto1 = new GroupResponseDto();
        GroupResponseDto dto2 = new GroupResponseDto();

        given(groupRepository.findAll()).willReturn(List.of(group1, group2));

        List<GroupResponseDto> result = groupService.getAllGroups();

        assertThat(result).hasSize(2).contains(dto1, dto2);
    }
}
