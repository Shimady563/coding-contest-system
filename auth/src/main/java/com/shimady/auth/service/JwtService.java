package com.shimady.auth.service;

import com.shimady.auth.model.User;
import com.shimady.auth.model.dto.JwtResponse;

public interface JwtService {
    JwtResponse generateToken(User user);

    JwtResponse refreshToken(String refreshToken);

    void deleteTokenByEmail(String email);
}
