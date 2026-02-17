package com.shimady.auth.service;

import com.shimady.auth.model.dto.JwtResponse;
import com.shimady.auth.model.dto.SignInJwtRequest;
import com.shimady.auth.model.dto.SignUpJwtRequest;
import com.shimady.auth.model.dto.UserResponse;

public interface AuthService {
    UserResponse getCurrentUser();

    JwtResponse signUp(SignUpJwtRequest request);

    JwtResponse authenticate(SignInJwtRequest request);

    JwtResponse refreshToken(String token);

    void logout();
}
