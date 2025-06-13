package com.shimady563.contest.manager.exception;

import org.springframework.dao.DataAccessException;

public class DataConflictException extends DataAccessException {
    public DataConflictException(String message) {
        super(message);
    }
}
