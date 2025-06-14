package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseService {
    private final TestCaseRepository testCaseRepository;

    @Transactional
    public void deleteByIds(List<Long> testCaseIds) {
        log.info("Removing tests cases with ids: {}", testCaseIds);
        testCaseRepository.deleteAllById(testCaseIds);
    }
}
