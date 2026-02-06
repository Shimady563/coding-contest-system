package com.shimady563.contest.manager.service.impl;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Slf4j
@RequiredArgsConstructor
public abstract class InternalUserService implements UserDetailsService {
    protected final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return getUserByEmail(username);
    }

    protected User getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " not found"));
    }

    protected User getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
    }

    protected User getCurrentUser() {
        return getUserByEmail(getCurrentUserEmail());
    }

    private static String getCurrentUserEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}
