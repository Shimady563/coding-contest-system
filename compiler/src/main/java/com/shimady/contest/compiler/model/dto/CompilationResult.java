package com.shimady.contest.compiler.model.dto;

import com.shimady.contest.compiler.model.SolutionStatus;

public record CompilationResult(
        SolutionStatus status,
        short testsPassed
) {
}
