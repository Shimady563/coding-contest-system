package com.shimady563.contest.manager.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DataConflictException extends DataIntegrityViolationException {
    public DataConflictException(String message) {
        super(message);
    }
}
