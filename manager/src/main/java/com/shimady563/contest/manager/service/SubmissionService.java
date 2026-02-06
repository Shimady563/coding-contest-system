package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.model.dto.CodeSubmissionDto;

public interface SubmissionService {
    void submitSolution(CodeSubmissionDto submissionDto);
}
