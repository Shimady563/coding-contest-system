package com.shimady.contest.compiler.service.impl;

import com.shimady.contest.compiler.converter.SolutionConverter;
import com.shimady.contest.compiler.model.Solution;
import com.shimady.contest.compiler.model.SolutionStatus;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.User;
import com.shimady.contest.compiler.model.dto.SolutionResponse;
import com.shimady.contest.compiler.repository.SolutionRepository;
import com.shimady.contest.compiler.service.SolutionService;
import com.shimady.contest.compiler.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionServiceImpl implements SolutionService {
    private final SolutionRepository solutionRepository;
    private final TaskService taskService;

    @Override
    @Transactional
    public void createSolution(
            String code,
            LocalDateTime submittedAt,
            SolutionStatus status,
            Short testsPassed,
            Task task,
            User user
    ) {
        log.info("Creating solution for task: {}, with status: {}", task.getId(), status);
        Solution solution = new Solution();
        solution.setCode(code);
        solution.setStatus(status);
        solution.setTestsPassed(testsPassed);
        solution.setSubmittedAt(submittedAt);
        user.addSolution(solution);
        task.addSolution(solution);
        solutionRepository.save(solution);
    }

    @Override
    @Transactional
    public List<SolutionResponse> getSolutionsByTaskId(Long taskId) {
        log.info("Searching for solutions with task id: {}", taskId);
        Task task = taskService.getTaskById(taskId);
        return solutionRepository.findAllByTask(task).stream()
                .map(SolutionConverter::domain2Response)
                .toList();
    }
}
