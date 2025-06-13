package com.shimady.auth.controller;

import com.shimady.auth.model.dto.JwtResponse;
import com.shimady.auth.model.dto.SignInJwtRequest;
import com.shimady.auth.model.dto.SignUpJwtRequest;
import com.shimady.auth.model.dto.UserResponse;
import com.shimady.auth.service.AuthService;
import com.shimady.auth.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Value("${jwt.token.access.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.token.refresh.expiration}")
    private Long refreshTokenExpiration;

    @Value("${jwt.token.access.cookie.name}")
    private String accessTokenCookieName;

    @Value("${jwt.token.refresh.cookie.name}")
    private String refreshTokenCookieName;

    @Secured({"ROLE_TEACHER", "ROLE_STUDENT"})
    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        return authService.getCurrentUser();
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpJwtRequest request) {
        JwtResponse response = authService.signUp(request);
        return buildResponse(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> signIn(@RequestBody SignInJwtRequest request) {
        JwtResponse response = authService.authenticate(request);
        return buildResponse(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(@CookieValue(value = "${jwt.token.refresh.cookie.name}") String refreshToken) {
        JwtResponse response = authService.refreshToken(refreshToken);
        return buildResponse(response);
    }

    private ResponseEntity<Void> buildResponse(JwtResponse response) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, JwtUtils.createCookieFromToken(accessTokenCookieName, response.getAccessToken(), accessTokenExpiration))
                .header(HttpHeaders.SET_COOKIE, JwtUtils.createCookieFromToken(refreshTokenCookieName, response.getRefreshToken(), refreshTokenExpiration))
                .build();
    }
}
