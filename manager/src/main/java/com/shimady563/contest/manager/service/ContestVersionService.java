package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.repository.ContestVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestVersionService {
    private final ContestVersionRepository contestVersionRepository;

    @Transactional(readOnly = true)
    protected ContestVersion getContestVersionById(Long id) {
        log.info("Getting contest version by id: {}", id);
        return contestVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest version with id: " + id + " not found"));
    }
}
