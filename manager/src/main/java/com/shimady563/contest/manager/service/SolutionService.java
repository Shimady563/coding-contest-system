package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Solution;
import com.shimady563.contest.manager.model.Status;
import com.shimady563.contest.manager.model.dto.SolutionResponseDto;
import com.shimady563.contest.manager.repository.SolutionRepository;
import com.shimady563.contest.manager.specification.SolutionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionService {
    private final SolutionRepository solutionRepository;
    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public Page<SolutionResponseDto> searchForSolutions(Status status, Long userId, Long contestId, LocalDateTime startDateTime, LocalDateTime endDateTime, PageRequest pageRequest) {
        StringBuilder logMessage = new StringBuilder().append("Searching for solutions with ");
        Specification<Solution> specification = Specification.where(null);

        if (status != null) {
            specification.and(SolutionSpecification.hasStatus(status));
            logMessage.append("status: ").append(status).append(", ");
        }

        if (userId != null) {
            specification.and(SolutionSpecification.hasUserId(userId));
            logMessage.append("user id: ").append(userId).append(", ");
        }

        if (contestId != null) {
            specification.and(SolutionSpecification.hasContestId(contestId));
            logMessage.append("contest id: ").append(contestId).append(", ");
        }

        if (startDateTime != null) {
            specification.and(SolutionSpecification.hasSubmittedAtAfter(startDateTime));
            logMessage.append("submitted at after: ").append(startDateTime).append(", ");
        }

        if (endDateTime != null) {
            specification.and(SolutionSpecification.hasSubmittedAtBefore(endDateTime));
            logMessage.append("submitted at before: ").append(endDateTime).append(", ");
        }

        int length = logMessage.length();

        if (logMessage.charAt(length - 2) == ',') {
            logMessage.delete(length - 2, length);
        } else {
            logMessage.delete(length - 6, length);
        }

        log.info(logMessage.toString());
        return solutionRepository.findAll(specification, pageRequest)
                .map(s -> mapper.map(s, SolutionResponseDto.class));
    }

    @Transactional(readOnly = true)
    public SolutionResponseDto getSolutionById(Long id) {
        log.info("Getting solution by id: {}", id);
        return mapper.map(solutionRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Solution with id: " + id + " not found")),
                SolutionResponseDto.class);
    }
}
