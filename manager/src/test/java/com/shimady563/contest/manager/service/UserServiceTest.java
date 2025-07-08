package com.shimady563.contest.manager.service;

import com.shimady563.contest.manager.exception.AccessDeniedException;
import com.shimady563.contest.manager.exception.DataConflictException;
import com.shimady563.contest.manager.exception.ResourceNotFoundException;
import com.shimady563.contest.manager.model.*;
import com.shimady563.contest.manager.model.dto.UserRegistrationRequestDto;
import com.shimady563.contest.manager.model.dto.UserResponseDto;
import com.shimady563.contest.manager.model.dto.UserUpdateRequestDto;
import com.shimady563.contest.manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupService groupService;

    @Mock
    private ContestService contestService;

    @Spy
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Group group = new Group();
        group.setId(1L);
        user.setGroup(group);
    }

    @Test
    void shouldGetUserByEmail() {
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));

        User result = userService.getUserByEmail("test@example.com");

        assertEquals(user, result);
    }

    @Test
    void shouldThrowWhenUserWithEmailDoesNotExist() {
        given(userRepository.findByEmail("notfound@example.com")).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByEmail("notfound@example.com")
        );
    }

    @Test
    void shouldGetUserById() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(user, result);
    }

    @Test
    void shouldThrowWhenUserWithIdDoesNotExist() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(99L)
        );
    }

    @Test
    void shouldUpdateUserById() {
        Long newUserId = 2L;
        UserUpdateRequestDto request = new UserUpdateRequestDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("new@example.com");
        request.setGroupId(newUserId);

        Group newGroup = new Group();
        newGroup.setId(newUserId);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(groupService.getGroupById(newUserId)).willReturn(newGroup);

        userService.updateUserById(1L, request);

        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getGroup()).isEqualTo(newGroup);
        then(userRepository).should().save(user);
    }

    @Test
    void shouldThrowWhenUserDoesNotHaveAccessToContestVersion() {
        Long anotherUserId = 2L;
        User anotherUser = new User();
        anotherUser.setId(anotherUserId);

        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setContestId(1L);
        dto.setContestVersionId(1L);

        given(userRepository.findById(anotherUserId)).willReturn(Optional.of(anotherUser));
        willReturn(user).given(userService).getCurrentUser();

        assertThrows(AccessDeniedException.class, () ->
                userService.registerUserForContestVersion(anotherUserId, dto)
        );

        then(contestService).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowWhenUserAlreadyStartedOtherContestVersionInCurrentContest() {
        ContestVersion existingVersion = new ContestVersion();
        existingVersion.setName("name1");
        existingVersion.setId(1L);
        ContestVersion newVersion = new ContestVersion();
        newVersion.setName("name2");
        newVersion.setId(2L);
        user.addContestVersion(existingVersion);

        Contest contest = new Contest();
        contest.addContestVersion(existingVersion);
        contest.addContestVersion(newVersion);

        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setContestId(1L);
        dto.setContestVersionId(2L);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(contestService.getContestByIdWithContestVersions(1L)).willReturn(contest);
        willReturn(user).given(userService).getCurrentUser();

        assertThrows(AccessDeniedException.class, () ->
                userService.registerUserForContestVersion(1L, dto)
        );
    }

    @Test
    void shouldThrowWhenContestVersionNotInCurrentContest() {
        ContestVersion existingVersion = new ContestVersion();
        existingVersion.setName("name1");
        existingVersion.setId(1L);
        user.addContestVersion(existingVersion);

        Contest contest = new Contest();
        contest.addContestVersion(existingVersion);

        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setContestId(1L);
        dto.setContestVersionId(2L);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(contestService.getContestByIdWithContestVersions(1L)).willReturn(contest);
        willReturn(user).given(userService).getCurrentUser();

        assertThrows(ResourceNotFoundException.class, () ->
                userService.registerUserForContestVersion(1L, dto)
        );
    }

    @Test
    void shouldDoNothingWhenUserAlreadyStartedSpecifiedContestVersion() {
        ContestVersion existingVersion = new ContestVersion();
        existingVersion.setId(1L);
        user.addContestVersion(existingVersion);

        Contest contest = new Contest();
        contest.setContestVersions(Set.of(existingVersion));

        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setContestId(1L);
        dto.setContestVersionId(1L);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        willReturn(user).given(userService).getCurrentUser();

        userService.registerUserForContestVersion(1L, dto);

        then(contestService).shouldHaveNoInteractions();
    }

    @Test
    void shouldRegisterUserForContestVersion() {
        Long contestId = 1L;
        Long contestVersionId = 2L;
        ContestVersion newVersion = new ContestVersion();
        newVersion.setId(contestVersionId);

        Contest contest = new Contest();
        contest.setId(contestId);
        contest.setContestVersions(Set.of(newVersion));

        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setContestId(contestId);
        dto.setContestVersionId(contestVersionId);

        given(userRepository.findById(contestId)).willReturn(Optional.of(user));
        given(contestService.getContestByIdWithContestVersions(contestId)).willReturn(contest);
        willReturn(user).given(userService).getCurrentUser();

        userService.registerUserForContestVersion(contestId, dto);

        assertThat(user.getContestVersions()).contains(newVersion);
    }

    @Test
    void shouldFindUsers() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));
        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setEmail(user.getEmail());

        given(userRepository.findAll(any(Specification.class), eq(pageRequest))).willReturn(userPage);

        Page<UserResponseDto> result = userService.searchForUsers(
                "John", "Doe", "john@example.com", Role.ROLE_STUDENT, "Group A", pageRequest
        );

        assertThat(result.getContent()).hasSize(1).containsExactly(response);
    }

    @Test
    void shouldDeleteUserById() {
        Long otherUserId = 100L;
        User otherUser = new User();
        otherUser.setId(otherUserId);

        willReturn(user).given(userService).getCurrentUser();
        userService.deleteUserById(otherUserId);

        then(userRepository).should().deleteById(otherUserId);
    }

    @Test
    void shouldThrowWhenTryingToDeleteCurrentUser() {
        willReturn(user).given(userService).getCurrentUser();

        assertThrows(DataConflictException.class,
                () -> userService.deleteUserById(user.getId()));

        then(userRepository).should(never()).deleteById(user.getId());
    }
}
