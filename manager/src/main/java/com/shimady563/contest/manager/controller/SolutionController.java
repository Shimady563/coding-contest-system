package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.model.Status;
import com.shimady563.contest.manager.model.dto.SolutionResponseDto;
import com.shimady563.contest.manager.service.SolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/solutions")
@RequiredArgsConstructor
public class SolutionController {
    private final SolutionService solutionService;

    @GetMapping("")
    @Secured({"ROLE_TEACHER"})
    public Page<SolutionResponseDto> searchForSolutions(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long contestId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return solutionService.searchForSolutions(status, userId, contestId, startTime, endTime, PageRequest.of(pageNumber, pageSize));
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_TEACHER", "ROLE_STUDENT"})
    public SolutionResponseDto getSolutionById(@PathVariable Long id) {
        return solutionService.getSolutionById(id);
    }
}
