package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.SolutionStatus;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.User;
import com.shimady.contest.compiler.model.dto.SolutionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface SolutionService {
    List<SolutionResponse> getSolutionsByTaskId(Long taskId);

    void createSolution(
            String code,
            LocalDateTime submittedAt,
            SolutionStatus status,
            Short testsPassed,
            Task task,
            User user
    );
}
