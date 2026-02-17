package com.shimady.auth.repository.impl;

import com.shimady.auth.exception.ResourceNotFoundException;
import com.shimady.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private static final String PREFIX = "refresh-token-";

    @Value("${jwt.token.refresh.expiration}")
    private Long refreshTokenExpiration;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String token, String email) {
        redisTemplate.opsForValue().set(PREFIX + email, token, refreshTokenExpiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public String findByEmail(String email) {
        if (!redisTemplate.hasKey(PREFIX + email)) {
            throw new ResourceNotFoundException("Refresh token for user with email: " + email + " not found");
        }
        return redisTemplate.opsForValue().get(PREFIX + email);
    }

    @Override
    public void deleteByEmail(String email) {
        redisTemplate.delete(PREFIX + email);
    }
}
