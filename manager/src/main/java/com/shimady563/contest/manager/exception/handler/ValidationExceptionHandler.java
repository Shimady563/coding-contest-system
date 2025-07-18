package com.shimady563.contest.manager.exception.handler;

import com.shimady563.contest.manager.converter.ViolationConverter;
import com.shimady563.contest.manager.exception.ValidationError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@Slf4j
@ControllerAdvice
public class ValidationExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationError> handleConstraintViolationException(ConstraintViolationException e) {
        List<ValidationError.Violation> violations = e.getConstraintViolations()
                .stream()
                .map(ViolationConverter::constraintViolation2Violation)
                .toList();
        log.info("Validation errors: {}", violations);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(violations, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ValidationError.Violation> violations = e.getBindingResult().getFieldErrors()
                .stream()
                .map(ViolationConverter::fieldError2Violation)
                .toList();
        log.info("Validation errors: {}", violations);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ValidationError(violations, HttpStatus.BAD_REQUEST.value()));
    }
}
