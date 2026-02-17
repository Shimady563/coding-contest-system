package com.shimady.auth.service.impl;

import com.shimady.auth.model.User;
import com.shimady.auth.model.dto.JwtResponse;
import com.shimady.auth.repository.RefreshTokenRepository;
import com.shimady.auth.service.JwtProvider;
import com.shimady.auth.service.JwtService;
import com.shimady.auth.service.UserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JwtProvider provider;
    private final RefreshTokenRepository tokenRepository;
    private final UserService userService;

    @Override
    @Transactional
    public JwtResponse generateToken(User user) {
        log.debug("Generating tokens for user with email: {}", user.getEmail());
        String refreshToken = provider.generateRefreshToken(user);
        tokenRepository.save(refreshToken, user.getEmail());
        return new JwtResponse(
                provider.generateAccessToken(user),
                refreshToken
        );
    }

    @Override
    @Transactional(readOnly = true)
    public JwtResponse refreshToken(String refreshToken) {
        log.debug("Refreshing access token");
        if (!provider.validateRefreshToken(refreshToken)) {
            throw new JwtException("Invalid refresh token");
        }

        User user = userService.getUserByEmail(provider.getEmailFromRefreshToken(refreshToken));
        String storedToken = tokenRepository.findByEmail(user.getEmail());

        if (!refreshToken.equals(storedToken)) {
            throw new JwtException("Invalid refresh token for user with email: " + user.getEmail());
        }

        String newRefreshToken = provider.generateRefreshToken(user);
        tokenRepository.save(newRefreshToken, user.getEmail());

        return new JwtResponse(
                provider.generateAccessToken(user),
                newRefreshToken
        );
    }

    @Override
    @Transactional
    public void deleteTokenByEmail(String email) {
        log.debug("Deleting refresh token for user with email: {}", email);
        tokenRepository.deleteByEmail(email);
    }
}
