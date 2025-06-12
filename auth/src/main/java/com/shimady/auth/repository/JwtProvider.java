package com.shimady.auth.repository;

import com.shimady.auth.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Repository
public class JwtProvider {
    @Value("${jwt.token.access.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.token.refresh.expiration}")
    private Long refreshTokenExpiration;

    private final SecretKey accessSecret;
    private final SecretKey refreshSecret;

    public JwtProvider(
            @Value("${jwt.token.access.secret}") String accessSecret,
            @Value("${jwt.token.refresh.secret}") String refreshSecret
    ) {
        this.accessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.refreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessTokenExpiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(accessSecret)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(refreshTokenExpiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(refreshSecret)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessSecret);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshSecret);
    }

    private boolean validateToken(String token, SecretKey secret) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);
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

    public String getEmailFromAccessToken(String token) {
        return getEmailFromToken(token, accessSecret);
    }

    public String getEmailFromRefreshToken(String token) {
        return getEmailFromToken(token, refreshSecret);
    }

    private String getEmailFromToken(String token, SecretKey secret) {
        return getClaimsFromToken(token, secret).getSubject();
    }

    public Claims getClaimsFromAccessToken(String token) {
        return getClaimsFromToken(token, accessSecret);
    }

    public Claims getClaimsFromRefreshToken(String token) {
        return getClaimsFromToken(token, refreshSecret);
    }

    private Claims getClaimsFromToken(String token, SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
