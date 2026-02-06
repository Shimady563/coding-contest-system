package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.model.Status;
import com.shimady563.contest.manager.model.dto.SolutionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface SolutionService {
    Page<SolutionResponseDto> searchForSolutions(Status status, Long userId, Long taskId, Long groupId, LocalDateTime startDateTime, LocalDateTime endDateTime, PageRequest pageRequest);

    SolutionResponseDto getSolutionById(Long id);

    List<SolutionResponseDto> getSolutionsByTaskId(Long taskId);
}
