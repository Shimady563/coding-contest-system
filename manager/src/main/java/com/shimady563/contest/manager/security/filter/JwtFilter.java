package com.shimady563.contest.manager.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shimady563.contest.manager.model.JwtAuthentication;
import com.shimady563.contest.manager.service.JwtProvider;
import com.shimady563.contest.manager.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    @Value("${auth.whitelist}")
    private final String[] whiteList;

    @Value("${jwt.token.access.cookie.name}")
    private String accessTokenCookieName;

    private final JwtProvider provider;
    private final ObjectMapper mapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("Filtering request: {}", request.getRequestURI());
        String token = JwtUtils.getTokenFromCookies(request.getCookies(), accessTokenCookieName);

        if (!provider.validateAccessToken(token)) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString("Jwt token is invalid or expired"));
            return;
        }

        log.info("Jwt token validated successfully");
        Claims claims = provider.getClaimsFromToken(token);
        JwtAuthentication authentication = JwtUtils.generateAuthentication(claims);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        for (String path : whiteList) {
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(path);
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }
}
