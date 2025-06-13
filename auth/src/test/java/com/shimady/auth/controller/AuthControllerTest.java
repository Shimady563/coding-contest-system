package com.shimady.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shimady.auth.TestSecurityConfig;
import com.shimady.auth.model.dto.JwtResponse;
import com.shimady.auth.model.dto.SignInJwtRequest;
import com.shimady.auth.model.dto.SignUpJwtRequest;
import com.shimady.auth.model.dto.UserResponse;
import com.shimady.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {
    @MockitoBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.token.access.cookie.name}")
    private String accessTokenCookieName;

    @Value("${jwt.token.refresh.cookie.name}")
    private String refreshTokenCookieName;

    @Test
    @WithMockUser(roles = {"TEACHER", "STUDENT"})
    void shouldGetCurrentUser() throws Exception {
        var userResponse = new UserResponse(1L, "John", "Doe", "test@example.com", "group", -1L);

        given(authService.getCurrentUser()).willReturn(userResponse);

        mockMvc.perform(get("/me"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponse)));
    }

    @Test
    @WithAnonymousUser
    public void shouldNotGetCurrentUserWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void shouldSignUpUser() throws Exception {
        var request = new SignUpJwtRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("Strongpassword123@");
        request.setEmail("test@example.com");
        request.setGroupId(1L);
        var response = new JwtResponse("token", "refreshToken");

        given(authService.signUp(any(SignUpJwtRequest.class))).willReturn(response);

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().value(accessTokenCookieName, response.getAccessToken()))
                .andExpect(cookie().value(refreshTokenCookieName, response.getRefreshToken()));
    }

    @Test
    @WithAnonymousUser
    void shouldSignInUser() throws Exception {
        var request = new SignInJwtRequest();
        request.setPassword("Strongpassword123@");
        request.setEmail("test@example.com");
        var response = new JwtResponse("token", "refreshToken");

        given(authService.authenticate(any(SignInJwtRequest.class))).willReturn(response);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().value(accessTokenCookieName, response.getAccessToken()))
                .andExpect(cookie().value(refreshTokenCookieName, response.getRefreshToken()));
    }

    @Test
    @WithAnonymousUser
    void shouldRefreshToken() throws Exception {
        var refreshToken = "refreshToken";
        Cookie cookie = new Cookie(refreshTokenCookieName, refreshToken);
        var response = new JwtResponse("newToken", "newRefreshToken");

        given(authService.refreshToken(refreshToken)).willReturn(response);

        mockMvc.perform(post("/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(cookie().value(accessTokenCookieName, response.getAccessToken()))
                .andExpect(cookie().value(refreshTokenCookieName, response.getRefreshToken()));
    }
}


