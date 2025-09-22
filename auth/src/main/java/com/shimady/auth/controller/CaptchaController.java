package com.shimady.auth.controller;

import com.shimady.auth.model.dto.CaptchaValidationRequest;
import com.shimady.auth.model.dto.CaptchaValidationResponse;
import com.shimady.auth.service.CaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("prod")
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {
    private final CaptchaService captchaService;

    @PostMapping("/validate")
    public CaptchaValidationResponse validateCaptcha(@RequestBody CaptchaValidationRequest payload,
                                                     HttpServletRequest request) {
        return captchaService.verifyCaptcha(payload, request.getRemoteAddr());
    }
}
