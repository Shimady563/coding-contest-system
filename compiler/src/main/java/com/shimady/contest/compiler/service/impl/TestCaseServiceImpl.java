package com.shimady.contest.compiler.service.impl;

import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.TestCase;
import com.shimady.contest.compiler.repository.TestCaseRepository;
import com.shimady.contest.compiler.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl implements TestCaseService {
    private final TestCaseRepository testCaseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TestCase> getAllTestCasesByTask(Task task) {
        log.info("Getting all test cases for task with id: {}", task.getId());
        return testCaseRepository.findAllByTask(task);
    }
}
