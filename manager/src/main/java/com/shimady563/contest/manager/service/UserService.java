package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.Role;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.UserResponse;
import com.shimady563.contest.manager.model.dto.UserUpdateRequest;
import com.shimady563.contest.manager.repository.UserRepository;
import com.shimady563.contest.manager.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final GroupService groupService;

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

    @Transactional(readOnly = true)
    public UserResponse getUserResponseById(Long id) {
        return mapper.map(getUserById(id), UserResponse.class);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> searchForUsers(String firstName, String lastName, String email, Role role, String groupName, PageRequest pageRequest) {
        StringBuilder logMessage = new StringBuilder().append("Searching for all users with ");
        Specification<User> specification = Specification.where(null);

        if (firstName != null) {
            specification.and(UserSpecification.hasFirstName(firstName));
            logMessage.append("first name: ").append(firstName).append(", ");
        }
        if (lastName != null) {
            specification.and(UserSpecification.hasLastName(lastName));
            logMessage.append("last name: ").append(lastName).append(", ");
        }
        if (email != null) {
            specification.and(UserSpecification.hasEmail(email));
            logMessage.append("email: ").append(email).append(", ");
        }
        if (role != null) {
            specification.and(UserSpecification.hasRole(role));
            logMessage.append("role: ").append(role).append(", ");
        }
        if (groupName != null) {
            specification.and(UserSpecification.hasGroupName(groupName));
            logMessage.append("group name: ").append(groupName).append(", ");
        }

        int length = logMessage.length();

        if (logMessage.charAt(length - 2) == ',') {
            logMessage.delete(length - 2, length);
        } else {
            logMessage.delete(length - 6, length);
        }

        log.info(logMessage.toString());
        return userRepository.findAllFetchGroup(specification, pageRequest)
                .map(u -> mapper.map(u, UserResponse.class));
    }

    @Transactional
    public void updateUserById(Long id, UserUpdateRequest request) {
        log.info("Updating user with id: {}", id);
        User user = getUserById(id);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        if (!user.getGroup().getId().equals(request.getGroupId())) {
            Group newGroup = groupService.getGroupById(request.getGroupId());
            user.setGroup(newGroup);
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return getUserByEmail(username);
    }
}
