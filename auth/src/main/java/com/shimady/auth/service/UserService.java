package com.shimady.auth.service;

import com.shimady.auth.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User getUserByEmail(String email);

    void saveUser(User user);

    default UserDetails loadUserByUsername(String username) {
        return getUserByEmail(username);
    }
}
