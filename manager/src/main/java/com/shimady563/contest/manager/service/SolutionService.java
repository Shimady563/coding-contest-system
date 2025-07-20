package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.converter.SolutionConverter;
import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Solution;
import com.shimady563.contest.manager.model.Status;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.SolutionResponseDto;
import com.shimady563.contest.manager.repository.SolutionRepository;
import com.shimady563.contest.manager.specification.SolutionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionService {
    private final SolutionRepository solutionRepository;
    private final UserService userService;
    private final TaskService taskService;

    @Transactional(readOnly = true)
    public Page<SolutionResponseDto> searchForSolutions(Status status, Long userId, Long taskId, LocalDateTime startDateTime, LocalDateTime endDateTime, PageRequest pageRequest) {
        StringBuilder logMessage = new StringBuilder().append("Searching for solutions with ");
        List<Specification<Solution>> specifications = new ArrayList<>();

        if (status != null) {
            specifications.add(SolutionSpecification.hasStatus(status));
            logMessage.append("status: ").append(status).append(", ");
        }

        if (userId != null) {
            specifications.add(SolutionSpecification.hasUserId(userId));
            logMessage.append("user id: ").append(userId).append(", ");
        }

        if (taskId != null) {
            specifications.add(SolutionSpecification.hasTaskId(taskId));
            logMessage.append("task id: ").append(taskId).append(", ");
        }

        if (startDateTime != null) {
            specifications.add(SolutionSpecification.hasSubmittedAtAfter(startDateTime));
            logMessage.append("submitted at after: ").append(startDateTime).append(", ");
        }

        if (endDateTime != null) {
            specifications.add(SolutionSpecification.hasSubmittedAtBefore(endDateTime));
            logMessage.append("submitted at before: ").append(endDateTime).append(", ");
        }

        int length = logMessage.length();

        if (logMessage.charAt(length - 2) == ',') {
            logMessage.delete(length - 2, length);
        } else {
            logMessage.delete(length - 6, length);
        }

        log.info(logMessage.toString());
        return solutionRepository.findAll(Specification.allOf(specifications), pageRequest)
                .map(SolutionConverter::domain2Response);
    }

    @Transactional(readOnly = true)
    public SolutionResponseDto getSolutionById(Long id) {
        log.info("Getting solution by id: {}", id);
        return solutionRepository.findById(id)
                .map(SolutionConverter::domain2Response)
                .orElseThrow(() -> new ResourceNotFoundException("Solution with id: " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<SolutionResponseDto> getSolutionsByTaskId(Long taskId) {
        log.info("Getting solutions for current user with task id: {}", taskId);
        User user = userService.getCurrentUser();
        Task task = taskService.getTaskById(taskId);
        return solutionRepository.findByUserAndTask(user, task).stream()
                .map(SolutionConverter::domain2Response)
                .toList();
    }
}
