package com.shimady.auth.service.impl;

import com.shimady.auth.config.props.JwtProperties;
import com.shimady.auth.model.User;
import com.shimady.auth.service.JwtProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
public class JwtProviderImpl implements JwtProvider {
    private final Long accessTokenExpiration;
    private final Long refreshTokenExpiration;
    private final SecretKey accessSecret;
    private final SecretKey refreshSecret;

    public JwtProviderImpl(JwtProperties jwtProperties) {
        this.accessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getAccess().getSecret()));
        this.refreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getRefresh().getSecret()));
        this.accessTokenExpiration = jwtProperties.getAccess().getExpiration();
        this.refreshTokenExpiration = jwtProperties.getRefresh().getExpiration();
    }

    @Override
    public String generateAccessToken(User user) {
        return generateToken(user, accessSecret, accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(User user) {
        return generateToken(user, refreshSecret, refreshTokenExpiration);
    }

    @Override
    public boolean validateAccessToken(String token) {
        return validateToken(token, accessSecret);
    }

    @Override
    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshSecret);
    }

    @Override
    public String getEmailFromRefreshToken(String token) {
        return getEmailFromToken(token, refreshSecret);
    }

    @Override
    public Claims getClaimsFromAccessToken(String token) {
        return getClaimsFromToken(token, accessSecret);
    }

    private String generateToken(User user, SecretKey secret, Long tokenExpiration) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(tokenExpiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secret)
                .compact();
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
            log.warn("Jwt token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Jwt token unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Jwt token malformed: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Jwt token has wrong signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid jwt token: {}", e.getMessage());
        }
        return false;
    }

    private String getEmailFromToken(String token, SecretKey secret) {
        return getClaimsFromToken(token, secret).getSubject();
    }

    private Claims getClaimsFromToken(String token, SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
