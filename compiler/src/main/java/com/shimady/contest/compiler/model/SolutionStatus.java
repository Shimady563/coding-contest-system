package com.shimady.contest.compiler.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SolutionStatus {
    TIMED_OUT("Timed Out"),
    COMPILE_ERROR("Compile Error"),
    RUNTIME_ERROR("Runtime Error"),
    WRONG_ANSWER("Wrong Answer"),
    ACCEPTED("Accepted"),
    INTERNAL_ERROR("Internal Error");

    private final String name;
}
