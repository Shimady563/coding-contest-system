package com.shimady563.contest.manager.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    TIMED_OUT("Timed Out"),
    COMPILE_ERROR("Compile Error"),
    RUNTIME_ERROR("Runtime Error"),
    WRONG_ANSWER("Wrong Answer"),
    ACCEPTED("Accepted");

    private final String name;
}
