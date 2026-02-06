package com.shimady563.contest.manager.service.impl;

import com.shimady563.contest.manager.converter.ContestConverter;
import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.dto.ContestRequestDto;
import com.shimady563.contest.manager.model.dto.ContestResponseDto;
import com.shimady563.contest.manager.repository.ContestRepository;
import com.shimady563.contest.manager.service.ContestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ContestServiceImpl extends InternalContestService implements ContestService {
    private final InternalGroupService groupService;

    public ContestServiceImpl(
            ContestRepository contestRepository,
            GroupServiceImpl groupService
    ) {
        super(contestRepository);
        this.groupService = groupService;
    }

    @Override
    @Transactional(readOnly = true)
    public ContestResponseDto getContestById(Long id) {
        return ContestConverter.domain2Response(getContestByIdInternal(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContestResponseDto> getContestsByName(String name, PageRequest pageRequest) {
        log.info("Getting contests by name: {}. Page number: {}", name, pageRequest.getPageNumber());
        return contestRepository.findByNameContainingIgnoreCase(name, pageRequest)
                .map(ContestConverter::domain2Response);
    }

    @Override
    @Transactional
    public ContestResponseDto createContest(ContestRequestDto request) {
        log.info("Creating contest from request: {}", request);
        Group group = groupService.getGroupById(request.getGroupId());
        Contest contest = ContestConverter.request2Domain(request);
        contest.setGroup(group);
        return ContestConverter.domain2Response(contestRepository.save(contest));
    }

    @Override
    @Transactional
    public void updateContestById(Long id, ContestRequestDto request) {
        log.info("Updating contest with id: {}, request: {}", id, request);
        Contest oldContest = getContestByIdInternal(id);

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

    @Override
    @Transactional(readOnly = true)
    public List<ContestResponseDto> getContestsByGroupId(Long groupId) {
        log.info("Getting contest by group id: {}", groupId);
        Group group = groupService.getGroupById(groupId);
        return contestRepository.findByGroup(group)
                .stream()
                .map(ContestConverter::domain2Response)
                .toList();
    }

    @Override
    @Transactional
    public void deleteContestById(Long id) {
        log.info("Deleting contest with id: {}", id);
        Contest contest = getContestByIdWithContestVersions(id);
        contest.getContestVersions().forEach(ContestVersion::removeUsers);
        contestRepository.delete(contest);
    }
}
