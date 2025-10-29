package com.shimady563.contest.manager.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class PasswordUpdateValidator {
    private final Validator validator;

    public boolean validateIfPresent(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }

        Set<ConstraintViolation<PasswordWrapper>> violations = validator.validate(new PasswordWrapper(password));
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return true;
    }

    private record PasswordWrapper(
            @Password(message = "password should be at least 8 characters long " +
                    "and contain at least one lower case letter, " +
                    "upper case letter, " +
                    "digit, " +
                    "symbol from @#$%^&+=!?*")
            String password
    ) {
    }
}
