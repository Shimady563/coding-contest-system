package com.shimady563.contest.manager.service;

import io.jsonwebtoken.Claims;

public interface JwtProvider {
    boolean validateAccessToken(String token);

    Claims getClaimsFromAccessToken(String token);
}
