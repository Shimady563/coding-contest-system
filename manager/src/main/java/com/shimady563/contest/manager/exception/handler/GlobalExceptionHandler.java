package com.shimady563.contest.manager.exception.handler;

import com.shimady563.contest.manager.exception.AccessDeniedException;
import com.shimady563.contest.manager.exception.AppError;
import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import jakarta.persistence.PersistenceException;
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

    @ExceptionHandler({DataIntegrityViolationException.class, PersistenceException.class})
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
        if (message == null) {
            return "";
        }
        if (message.contains("Detail:")) {
            String[] afterDetail = message.split("Detail:", 2);
            if (afterDetail.length > 1) {
                String trimmed = afterDetail[1];
                int end = trimmed.indexOf(']');
                return (end > 0 ? trimmed.substring(0, end) : trimmed).trim();
            }
        }
        int keyIdx = message.indexOf("Key (id)=");
        if (keyIdx >= 0) {
            String tail = message.substring(keyIdx);
            int bracket = tail.indexOf(']');
            return (bracket > 0 ? tail.substring(0, bracket) : tail).trim();
        }
        return message;
    }
}
