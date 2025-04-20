package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.exception.ResourceNotFoundException;
import com.shimady.contest.compiler.model.User;
import com.shimady.contest.compiler.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldGetTaskById() {
        var user = new User();
        user.setId(1L);
        user.setEmail("Email");
        user.setPassword("Password");

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        var foundTask = userService.getUserById(user.getId());

        assertEquals(user.getId(), foundTask.getId());
        assertEquals(user.getEmail(), foundTask.getEmail());
        assertEquals(user.getPassword(), foundTask.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundById() {
        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(1L));
    }
}
