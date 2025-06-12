package com.shimady563.contest.manager.utils;


import com.shimady563.contest.manager.model.JwtAuthentication;
import com.shimady563.contest.manager.model.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;

public class JwtUtils {
    public static JwtAuthentication generateAuthentication(Claims claims) {
        JwtAuthentication auth = new JwtAuthentication();
        auth.setEmail(claims.getSubject());
        auth.setRole(Role.valueOf(claims.get("role", String.class)));
        return auth;
    }

    public static String getTokenFromCookies(Cookie[] cookies, String tokenName) {
        Cookie tokenCookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals(tokenName))
                .findFirst()
                .orElse(null);
        return tokenCookie == null ? null : tokenCookie.getValue();
    }
}
