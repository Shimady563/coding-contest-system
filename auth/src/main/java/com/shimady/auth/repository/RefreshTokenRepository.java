package com.shimady.auth.repository;

public interface RefreshTokenRepository {
    void save(String token, String email);

    String findByEmail(String email);

    void deleteByEmail(String email);
}
