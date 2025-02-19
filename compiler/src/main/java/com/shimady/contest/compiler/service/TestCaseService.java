package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.exception.ResourceNotFoundException;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.TestCase;
import com.shimady.contest.compiler.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseService {
    private final TestCaseRepository testCaseRepository;
    private final TaskService taskService;

    @Transactional(readOnly = true)
    public TestCase getTestCaseByTaskId(Long id) {
        log.info("Getting test case by task id: {}", id);
        Task task = taskService.getTaskById(id);
        return testCaseRepository.findByTask(task)
                .orElseThrow(() -> new ResourceNotFoundException("Test case with id: " + id + " not found"));
    }
}
