package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.dto.ContestRequestDto;
import com.shimady563.contest.manager.model.dto.ContestResponseDto;
import com.shimady563.contest.manager.repository.ContestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ContestServiceTest {
    @Mock
    private GroupService groupService;

    @Mock
    private ContestRepository contestRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ContestService contestService;

    @Test
    void shouldGetContestsByName() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        String name = "name";
        Contest contest = new Contest();
        contest.setName(name);
        ContestResponseDto responseDto = new ContestResponseDto();

        given(contestRepository.findByNameContainingIgnoreCase(name, pageRequest))
                .willReturn(new PageImpl<>(List.of(contest)));
        given(mapper.map(contest, ContestResponseDto.class)).willReturn(responseDto);

        Page<ContestResponseDto> result = contestService.getContestsByName(name, pageRequest);

        assertThat(result.getContent()).hasSize(1).contains(responseDto);
    }

    @Test
    void shouldCreateContest() {
        Long groupId = 1L;
        ContestRequestDto request = new ContestRequestDto();
        request.setName("Contest");
        request.setGroupId(groupId);
        request.setDescription("Desc");
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusDays(1));

        Group group = new Group();
        Contest savedContest = new Contest();
        ContestResponseDto responseDto = new ContestResponseDto();

        given(groupService.getGroupById(groupId)).willReturn(group);
        given(contestRepository.save(any(Contest.class))).willReturn(savedContest);
        given(mapper.map(savedContest, ContestResponseDto.class)).willReturn(responseDto);

        ContestResponseDto result = contestService.createContest(request);

        assertNotNull(result);
    }

    @Test
    void shouldUpdateContestById() {
        Long groupId = 1L;
        Long newGroupId = 2L;
        ContestRequestDto request = new ContestRequestDto();
        String name = "Updated";
        request.setName(name);
        request.setDescription("Updated Desc");
        request.setGroupId(newGroupId);
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusDays(2));

        Group currentGroup = new Group();
        currentGroup.setId(groupId);
        Group newGroup = new Group();
        newGroup.setId(newGroupId);

        Contest existing = new Contest();
        existing.setId(groupId);
        existing.setGroup(currentGroup);

        given(contestRepository.findById(groupId)).willReturn(Optional.of(existing));
        given(groupService.getGroupById(newGroupId)).willReturn(newGroup);

        contestService.updateContestById(groupId, request);

        assertEquals(name, existing.getName());
        then(contestRepository).should().save(existing);
    }

    @Test
    void shouldGetContestsByGroupId() {
        Group group = new Group();
        Long groupId = 1L;
        group.setId(groupId);

        Contest contest = new Contest();
        ContestResponseDto dto = new ContestResponseDto();

        given(groupService.getGroupById(groupId)).willReturn(group);
        given(contestRepository.findByGroup(group)).willReturn(List.of(contest));
        given(mapper.map(contest, ContestResponseDto.class)).willReturn(dto);

        List<ContestResponseDto> result = contestService.getContestsByGroupId(groupId);

        assertThat(result).hasSize(1).containsExactly(dto);
    }

    @Test
    void shouldGetContestById() {
        Long id = 1L;
        Contest contest = new Contest();
        contest.setId(id);

        given(contestRepository.findById(id)).willReturn(Optional.of(contest));

        Contest result = contestService.getContestById(id);

        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenContestNotFoundById() {
        given(contestRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contestService.getContestById(999L));
    }

    @Test
    void shouldGetContestByIdWithContestVersions() {
        Long id = 1L;
        Contest contest = new Contest();
        contest.setId(id);

        given(contestRepository.findByIdWithContestVersions(id)).willReturn(Optional.of(contest));

        Contest result = contestService.getContestByIdWithContestVersions(id);

        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenContestWithVersionsNotFound() {
        given(contestRepository.findByIdWithContestVersions(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contestService.getContestByIdWithContestVersions(999L));
    }
}