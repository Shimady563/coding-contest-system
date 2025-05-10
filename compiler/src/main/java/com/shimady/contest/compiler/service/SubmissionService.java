package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.User;
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
    private final UserService userService;

    @Transactional
    public void submitSolution(CodeSubmission submission) {
        log.info("Submitting solution for task with id: {}, user id: {}",
                submission.getTaskId(),
                submission.getUserId());
        Task task = taskService.getTaskById(submission.getTaskId());
        User user = userService.getUserById(submission.getUserId());
        compilerService.compileAndRun(submission.getCode(), submission.getSubmittedAt(), task, user);
    }
}
