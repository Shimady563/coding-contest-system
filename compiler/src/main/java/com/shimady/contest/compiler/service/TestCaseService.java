package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.TestCase;
import com.shimady.contest.compiler.repository.TestCaseRepository;
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

    @Transactional(readOnly = true)
    public List<TestCase> getAllTestCasesByTask(Task task) {
        log.info("Getting all test cases for task with id: {}", task.getId());
        return testCaseRepository.findAllByTask(task);
    }
}
