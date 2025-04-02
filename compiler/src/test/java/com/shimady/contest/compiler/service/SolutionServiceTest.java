package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.Solution;
import com.shimady.contest.compiler.model.Status;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.repository.SolutionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        var task = new Task();
        task.setName("Task");

        solutionService.createSolution(code, status, (short) testsPassed, task);

        then(solutionRepository).should().save(any(Solution.class));
    }
}
