package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.exception.ResourceNotFoundException;
import com.shimady.contest.compiler.model.User;
import com.shimady.contest.compiler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found"));
    }
}
