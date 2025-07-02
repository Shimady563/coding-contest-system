package com.shimady563.contest.manager.exception;

import java.util.List;

public record ValidationError(List<Violation> violations, int statusCode) {
    public record Violation(String field, String message) {
    }
}
