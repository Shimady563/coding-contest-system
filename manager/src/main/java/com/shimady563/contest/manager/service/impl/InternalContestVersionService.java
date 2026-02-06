package com.shimady563.contest.manager.service.impl;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.repository.ContestVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class InternalContestVersionService {
    protected final ContestVersionRepository contestVersionRepository;

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
}
