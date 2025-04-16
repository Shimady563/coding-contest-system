package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.model.dto.CodeSubmissionDto;
import com.shimady563.contest.manager.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
@Secured({"ROLE_STUDENT", "ROLE_TEACHER"})
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void submitSolution(@Valid @RequestBody CodeSubmissionDto submission) {
        submissionService.submitSolution(submission);
    }
}
