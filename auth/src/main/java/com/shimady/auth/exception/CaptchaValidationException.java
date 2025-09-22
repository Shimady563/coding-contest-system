package com.shimady.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class CaptchaValidationException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public CaptchaValidationException(HttpStatusCode statusCode) {
        super();
        this.statusCode = statusCode;
    }
}
