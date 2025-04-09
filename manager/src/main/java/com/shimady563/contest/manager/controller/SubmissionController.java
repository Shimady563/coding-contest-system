package com.shimady563.contest.manager.controller;

import com.shimady563.contest.manager.model.dto.CodeSubmission;
import com.shimady563.contest.manager.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
@Secured({"ROLE_STUDENT", "ROLE_TEACHER"})
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping("")
    public void submitSolution(@Valid @RequestBody CodeSubmission submission) {
        submissionService.submitSolution(submission);
    }
}
