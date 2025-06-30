package com.shimady563.contest.manager.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AppError> handleAccessDeniedException(AccessDeniedException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new AppError(e.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<AppError> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new AppError(e.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<AppError> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new AppError(parseDataAccessExceptionMessage(e.getMessage()), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<AppError> handleDataAccessException(DataAccessException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AppError(parseDataAccessExceptionMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    private String parseDataAccessExceptionMessage(String message) {
        if (message.contains("Detail:")) {
            return message.split("Detail:")[1].split("]")[0].trim();
        }
        return message;
    }
}
