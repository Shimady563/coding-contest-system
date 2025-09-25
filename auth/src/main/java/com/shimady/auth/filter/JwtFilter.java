package com.shimady.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shimady.auth.config.props.AuthProperties;
import com.shimady.auth.config.props.JwtProperties;
import com.shimady.auth.model.JwtAuthentication;
import com.shimady.auth.repository.JwtProvider;
import com.shimady.auth.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;
    private final JwtProvider provider;
    private final ObjectMapper mapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("Filtering request: {}", request.getRequestURI());
        String token = JwtUtils.getTokenFromCookies(request.getCookies(), jwtProperties.getAccess().getCookieName());

        if (!provider.validateAccessToken(token)) {
            SecurityContextHolder.clearContext();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString("Jwt token is invalid or expired"));
            return;
        }

        log.info("Jwt token validated successfully");
        Claims claims = provider.getClaimsFromAccessToken(token);
        JwtAuthentication authentication = JwtUtils.generateAuthentication(claims);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        for (String path : authProperties.getWhitelist()) {
            PathPatternRequestMatcher matcher = PathPatternRequestMatcher.withDefaults().matcher(path);
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }
}
