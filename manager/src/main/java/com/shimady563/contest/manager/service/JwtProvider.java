package com.shimady563.contest.manager.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.security.Key;

@Slf4j
@Repository
public class JwtProvider {
    @Value("${jwt.token.access.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.token.refresh.expiration}")
    private Long refreshTokenExpiration;

    private final Key accessSecret;
    private final Key refreshSecret;

    public JwtProvider(
            @Value("${jwt.token.access.secret}") String accessSecret,
            @Value("${jwt.token.refresh.secret}") String refreshSecret
    ) {
        this.accessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.refreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessSecret);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshSecret);
    }

    private boolean validateToken(String token, Key secret) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Jwt token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Jwt token unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Jwt token malformed: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("Jwt token has wrong signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid jwt token: {}", e.getMessage());
        }
        return false;
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
