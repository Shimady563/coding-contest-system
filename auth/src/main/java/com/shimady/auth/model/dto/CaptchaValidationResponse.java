package com.shimady.auth.model.dto;

public record CaptchaValidationResponse(
        String status,
        String message,
        String host
) {
}
