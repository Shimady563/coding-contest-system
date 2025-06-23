package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.model.dto.ContestVersionRequestDto;
import com.shimady563.contest.manager.model.dto.ContestVersionResponseDto;
import com.shimady563.contest.manager.service.ContestVersionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contest-versions")
@RequiredArgsConstructor
public class ContestVersionController {
    private final ContestVersionService contestVersionService;

    @GetMapping("")
    @Secured({"ROLE_STUDENT", "ROLE_TEACHER"})
    public List<ContestVersionResponseDto> getContestVersionsByContestId(@RequestParam Long contestId) {
        return contestVersionService.getContestVersionsByContestId(contestId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({"ROLE_TEACHER"})
    public void createContestVersion(@Valid @RequestBody ContestVersionRequestDto request) {
        contestVersionService.createContestVersion(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_TEACHER"})
    public void deleteContestVersionById(@PathVariable Long id) {
        contestVersionService.deleteContestVersionById(id);
    }
}
