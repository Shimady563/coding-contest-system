package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    protected User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return getUserByEmail(username);
    }
}
