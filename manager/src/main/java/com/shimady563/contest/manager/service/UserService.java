package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.model.Role;
import com.shimady563.contest.manager.model.dto.UserRegistrationRequestDto;
import com.shimady563.contest.manager.model.dto.UserResponseDto;
import com.shimady563.contest.manager.model.dto.UserUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface UserService {
    UserResponseDto getUserResponseById(Long id);

    void updateUserById(Long id, UserUpdateRequestDto request);

    void deleteUserById(Long id);

    void registerUserForContestVersion(Long id, UserRegistrationRequestDto request);

    Page<UserResponseDto> searchForUsers(
            String firstName,
            String lastName,
            String email,
            Role role, String groupName,
            PageRequest pageRequest
    );
}
