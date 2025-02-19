package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.dto.CodeSubmission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final CompilerService compilerService;
    private final TaskService taskService;

    @Transactional
    public void submitSolution(CodeSubmission submission) {
        log.info("Submitting solution for task with id: {}", submission.getTaskId());
        Task task = taskService.getTaskById(submission.getTaskId());
        compilerService.compileAndRun(submission.getCode(), task);
    }
}
