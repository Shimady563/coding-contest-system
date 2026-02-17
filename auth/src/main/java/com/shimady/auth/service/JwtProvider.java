package com.shimady.auth.service;

import com.shimady.auth.model.User;
import io.jsonwebtoken.Claims;

public interface JwtProvider {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    boolean validateAccessToken(String token);

    boolean validateRefreshToken(String token);

    String getEmailFromRefreshToken(String token);

    Claims getClaimsFromAccessToken(String token);
}
