package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.dto.CodeSubmission;

public interface SubmissionService {
    void submitSolution(CodeSubmission submission);
}
