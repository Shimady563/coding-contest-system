package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.dto.CodeSubmission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class SubmissionServiceTest {
    @Mock
    private CompilerService compilerService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private SubmissionService submissionService;

    @Test
    void shouldSubmitSolution() {
        var submission = new CodeSubmission();
        submission.setCode("Code");
        submission.setTaskId(1L);
        var task = new Task();
        task.setId(submission.getTaskId());
        task.setName("Task");

        given(taskService.getTaskById(task.getId())).willReturn(task);

        submissionService.submitSolution(submission);

        then(compilerService).should().compileAndRun(submission.getCode(), task);
    }
}
