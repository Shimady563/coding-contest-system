package com.shimady.auth.service.impl;

import com.shimady.auth.converter.AuthConverter;
import com.shimady.auth.model.Group;
import com.shimady.auth.model.User;
import com.shimady.auth.model.dto.JwtResponse;
import com.shimady.auth.model.dto.SignInJwtRequest;
import com.shimady.auth.model.dto.SignUpJwtRequest;
import com.shimady.auth.model.dto.UserResponse;
import com.shimady.auth.service.AuthService;
import com.shimady.auth.service.GroupService;
import com.shimady.auth.service.JwtService;
import com.shimady.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final GroupService groupService;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User user = userService.getUserByEmail(getUserEmail());
        return AuthConverter.domain2Response(user);
    }

    @Override
    @Transactional
    public JwtResponse signUp(SignUpJwtRequest request) {
        log.info("Signing up user with email: {}", request.getEmail());
        User user = AuthConverter.signUpRequest2Domain(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Group group = groupService.getGroupById(request.getGroupId());
        group.addUser(user);
        userService.saveUser(user);
        return jwtService.generateToken(user);
    }

    @Override
    @Transactional(readOnly = true)
    public JwtResponse authenticate(SignInJwtRequest request) {
        log.info("Authenticating user with email: {}", request.getEmail());
        User user = userService.getUserByEmail(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password for user: " + user.getEmail());
        }
        return jwtService.generateToken(user);
    }

    @Override
    @Transactional
    public JwtResponse refreshToken(String token) {
        return jwtService.refreshToken(token);
    }

    @Override
    @Transactional
    public void logout() {
        String email = getUserEmail();
        log.info("Logging out user with email: {}", email);
        jwtService.deleteTokenByEmail(email);
    }

    protected String getUserEmail() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
