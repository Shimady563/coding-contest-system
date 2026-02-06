package com.shimady563.contest.manager.service.impl;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class InternalContestService {
    protected final ContestRepository contestRepository;

    protected Contest getContestByIdInternal(Long id) {
        log.info("Getting contest by id: {}", id);
        return contestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest with id: " + id + " not found"));
    }

    protected Contest getContestByIdWithContestVersions(Long id) {
        log.info("Getting contest with contest versions by id: {}", id);
        return contestRepository.findByIdWithContestVersions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest with id: " + id + " not found"));
    }
}
