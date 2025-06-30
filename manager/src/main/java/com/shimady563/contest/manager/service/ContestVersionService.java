package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.converter.ContestVersionConverter;
import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.dto.ContestVersionRequestDto;
import com.shimady563.contest.manager.model.dto.ContestVersionResponseDto;
import com.shimady563.contest.manager.repository.ContestVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestVersionService {
    private final ContestVersionRepository contestVersionRepository;
    private final ContestService contestService;
    private final TaskService taskService;

    protected ContestVersion getContestVersionById(Long id) {
        log.info("Getting contest version by id: {}", id);
        return contestVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest version with id: " + id + " not found"));
    }

    protected ContestVersion getContestVersionWithUsersById(Long id) {
        log.info("Getting contest version with users by id: {}", id);
        return contestVersionRepository.findByIdFetchUsers(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest version with id: " + id + " not found"));
    }

    @Transactional
    public void createContestVersion(ContestVersionRequestDto request) {
        log.info("Creating contest version from request: {}", request);
        ContestVersion contestVersion = ContestVersionConverter.request2Domain(request);
        Contest contest = contestService.getContestById(request.getContestId());
        List<Task> tasks = taskService.getTasksByIds(request.getTaskIds());
        contest.addContestVersion(contestVersion);
        tasks.forEach(contestVersion::addTask);
        contestVersionRepository.save(contestVersion);
    }

    @Transactional(readOnly = true)
    public List<ContestVersionResponseDto> getContestVersionsByContestId(Long contestId) {
        log.info("Getting contest versions by contest id: {}", contestId);
        Contest contest = contestService.getContestById(contestId);
        return contestVersionRepository.findByContest(contest)
                .stream()
                .map(ContestVersionConverter::domain2Response)
                .toList();
    }

    @Transactional
    public void deleteContestVersionById(Long id) {
        log.info("Deleting contest version with id: {}", id);
        ContestVersion contestVersion = getContestVersionById(id);
        contestVersion.removeUsers();
        contestVersionRepository.delete(contestVersion);

    }
}
