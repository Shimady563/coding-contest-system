package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.dto.ContestRequestDto;
import com.shimady563.contest.manager.model.dto.ContestResponseDto;
import com.shimady563.contest.manager.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestService {
    private final GroupService groupService;
    private final ContestRepository contestRepository;
    private final ModelMapper mapper;

    protected Contest getContestById(Long id) {
        log.info("Getting contest by id: {}", id);
        return contestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest with id: " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Page<ContestResponseDto> getContestsByName(String name, PageRequest pageRequest) {
        log.info("Getting contests by name: {}. Page number: {}", name, pageRequest.getPageNumber());
        return contestRepository.findByNameContainingIgnoreCase(name, pageRequest)
                .map(c -> mapper.map(c, ContestResponseDto.class));
    }

    @Transactional
    public ContestResponseDto createContest(ContestRequestDto request) {
        log.info("Creating contest from request: {}", request);
        Group group = groupService.getGroupById(request.getGroupId());

        Contest contest = new Contest();
        contest.setName(request.getName());
        contest.setGroup(group);
        contest.setDescription(request.getDescription());
        contest.setStartTime(request.getStartTime());
        contest.setEndTime(request.getEndTime());
        return mapper.map(contestRepository.save(contest), ContestResponseDto.class);
    }

    @Transactional
    public void updateContestById(Long id, ContestRequestDto request) {
        log.info("Updating contest with id: {}, request: {}", id, request);
        Contest oldContest = getContestById(id);

        if (!request.getGroupId().equals(oldContest.getGroup().getId())) {
            Group group = groupService.getGroupById(request.getGroupId());
            oldContest.setGroup(group);
        }

        oldContest.setName(request.getName());
        oldContest.setDescription(request.getDescription());
        oldContest.setStartTime(request.getStartTime());
        oldContest.setEndTime(request.getEndTime());
        contestRepository.save(oldContest);
    }

    @Transactional(readOnly = true)
    public List<ContestResponseDto> getContestsByGroupId(Long groupId) {
        log.info("Getting contest by group id: {}", groupId);
        Group group = groupService.getGroupById(groupId);
        return contestRepository.findByGroup(group)
                .stream()
                .map(cv -> mapper.map(cv, ContestResponseDto.class))
                .toList();
    }

    @Transactional
    public void deleteContestById(Long id) {
        log.info("Deleting contest with id: {}", id);
        Contest contest = getContestByIdWithContestVersions(id);
        contest.getContestVersions().forEach(ContestVersion::removeUsers);
        contestRepository.delete(contest);
    }

    protected Contest getContestByIdWithContestVersions(Long id) {
        log.info("Getting contest with contest versions by id: {}", id);
        return contestRepository.findByIdWithContestVersions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest with id: " + id + " not found"));
    }
}
