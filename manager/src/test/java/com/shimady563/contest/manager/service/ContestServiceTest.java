package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.dto.ContestRequestDto;
import com.shimady563.contest.manager.model.dto.ContestResponseDto;
import com.shimady563.contest.manager.repository.ContestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ContestServiceTest {
    @Mock
    private GroupService groupService;

    @Mock
    private ContestRepository contestRepository;

    @InjectMocks
    private ContestService contestService;

    @Test
    void shouldGetContestsByName() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        String name = "name";
        Contest contest = new Contest();
        contest.setName(name);
        ContestResponseDto responseDto = new ContestResponseDto();
        responseDto.setName(name);

        given(contestRepository.findByNameContainingIgnoreCase(name, pageRequest))
                .willReturn(new PageImpl<>(List.of(contest)));

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

    @Test
    void shouldDeleteContestById() {
        Long contestId = 1L;
        Contest contest = new Contest();
        contest.setId(contestId);
        ContestVersion version1 = new ContestVersion();
        version1.setName("name1");
        ContestVersion version2 = new ContestVersion();
        version2.setName("name2");

        contest.setContestVersions(Set.of(version1, version2));

        given(contestRepository.findByIdWithContestVersions(contestId)).willReturn(Optional.of(contest));

        contestService.deleteContestById(contestId);

        then(contestRepository).should().delete(contest);
    }

    @Test
    void shouldThrowWhileDeletingWhenContestNotFoundById() {
        Long contestId = 99L;
        given(contestRepository.findByIdWithContestVersions(contestId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contestService.deleteContestById(contestId));

        then(contestRepository).should(never()).delete(any());
    }
}