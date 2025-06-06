package com.shimady563.contest.manager.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    ROLE_TEACHER("Teacher"),
    ROLE_STUDENT("Student");

    private final String value;

    @Override
    public String getAuthority() {
        return name();
    }
}
