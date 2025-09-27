package com.shimady.auth.controller;

import com.shimady.auth.config.props.JwtProperties;
import com.shimady.auth.model.dto.JwtResponse;
import com.shimady.auth.model.dto.SignInJwtRequest;
import com.shimady.auth.model.dto.SignUpJwtRequest;
import com.shimady.auth.model.dto.UserResponse;
import com.shimady.auth.service.AuthService;
import com.shimady.auth.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtProperties jwtProperties;

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
    public ResponseEntity<Void> signIn(@Valid @RequestBody SignInJwtRequest request) {
        JwtResponse response = authService.authenticate(request);
        return buildResponse(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(@CookieValue(value = "${jwt.token.refresh.cookie-name}") String refreshToken) {
        JwtResponse response = authService.refreshToken(refreshToken);
        return buildResponse(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        JwtUtils.removeTokenCookie(jwtProperties.getAccess().getCookieName())
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        JwtUtils.removeTokenCookie(jwtProperties.getRefresh().getCookieName())
                )
                .build();
    }

    private ResponseEntity<Void> buildResponse(JwtResponse response) {
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        JwtUtils.createTokenCookie(
                                jwtProperties.getAccess().getCookieName(),
                                response.getAccessToken(),
                                jwtProperties.getAccess().getExpiration()
                        )
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        JwtUtils.createTokenCookie(
                                jwtProperties.getRefresh().getCookieName(),
                                response.getRefreshToken(),
                                jwtProperties.getRefresh().getExpiration()
                        )
                )
                .build();
    }
}
