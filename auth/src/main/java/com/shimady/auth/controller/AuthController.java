package com.shimady.auth.controller;

import com.shimady.auth.model.dto.*;
import com.shimady.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Secured({"ROLE_TEACHER", "ROLE_STUDENT"})
    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        return authService.getCurrentUser();
    }

    @PostMapping("/signup")
    public JwtResponse signUp(@Valid @RequestBody SignUpJwtRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/login")
    public JwtResponse signIn(@RequestBody SignInJwtRequest request) {
        return authService.authenticate(request);
    }

    @PostMapping("/refresh")
    public JwtResponse refreshToken(@Valid @RequestBody RefreshJwtRequest request) {
        return authService.refreshToken(request);
    }
}
