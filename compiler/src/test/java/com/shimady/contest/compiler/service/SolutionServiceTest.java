package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.Solution;
import com.shimady.contest.compiler.model.Status;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.User;
import com.shimady.contest.compiler.repository.SolutionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class SolutionServiceTest {
    @Mock
    private SolutionRepository solutionRepository;

    @InjectMocks
    private SolutionService solutionService;

    @Test
    void shouldCreateSolution() {
        var testsPassed = 0;
        var code = "Code";
        var status = Status.ACCEPTED;
        var submittedAt = LocalDateTime.now();
        var task = new Task();
        task.setName("Task");
        var user = new User();
        user.setEmail("Email");
        user.setPassword("Password");

        solutionService.createSolution(code, submittedAt, status, (short) testsPassed, task, user);

        then(solutionRepository).should().save(any(Solution.class));
    }
}
