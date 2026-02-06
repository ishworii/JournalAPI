package com.ishwor.journalapi.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ishwor.journalapi.dto.*;
import com.ishwor.journalapi.entity.RefreshTokenEntity;
import com.ishwor.journalapi.entity.UserEntity;
import com.ishwor.journalapi.entity.Role;
import com.ishwor.journalapi.exception.RefreshTokenException;
import com.ishwor.journalapi.service.AuthService;
import com.ishwor.journalapi.service.JwtService;
import com.ishwor.journalapi.service.RefreshTokenService;
import com.ishwor.journalapi.service.impl.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CurrentUserService currentUserService;

    @Test
    public void shouldRegisterUser_WithValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setRole("USER");

        AuthResponse response = new AuthResponse("mock-access-token", "mock-refresh-token");

        Mockito.when(authService.register(Mockito.any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"));
    }

    @Test
    public void shouldLoginUser_WithValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse("mock-access-token", "mock-refresh-token");

        Mockito.when(authService.login(Mockito.any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    public void shouldBeAccessibleWithoutAuthentication_Register() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("public@example.com");
        request.setPassword("password123");
        request.setRole("USER");

        AuthResponse response = new AuthResponse("public-access-token", "public-refresh-token");

        Mockito.when(authService.register(Mockito.any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldBeAccessibleWithoutAuthentication_Login() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("public@example.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse("public-access-token", "public-refresh-token");

        Mockito.when(authService.login(Mockito.any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnTokens_WhenRegisteringWithAdminRole() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("admin@example.com");
        request.setPassword("adminpass123");
        request.setRole("ADMIN");

        AuthResponse response = new AuthResponse("admin-access-token", "admin-refresh-token");

        Mockito.when(authService.register(Mockito.any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("admin-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("admin-refresh-token"));
    }

    // ============ REFRESH TOKEN TESTS ============

    @Test
    public void shouldRefreshToken_WithValidRefreshToken() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setRole(Role.USER);

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setToken("valid-refresh-token");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(86400));

        RefreshTokenEntity newRefreshToken = new RefreshTokenEntity();
        newRefreshToken.setToken("new-refresh-token");
        newRefreshToken.setUser(user);
        newRefreshToken.setExpiryDate(Instant.now().plusSeconds(86400));

        Mockito.when(refreshTokenService.findByToken("valid-refresh-token"))
                .thenReturn(Optional.of(refreshToken));
        Mockito.when(refreshTokenService.verifyExpiration(refreshToken))
                .thenReturn(refreshToken);
        Mockito.when(jwtService.generateToken(1L, "user@example.com", Role.USER))
                .thenReturn("new-access-token");
        Mockito.when(refreshTokenService.createRefreshToken("user@example.com"))
                .thenReturn(newRefreshToken);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    public void shouldReturnError_WhenRefreshTokenNotFound() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid-token");

        Mockito.when(refreshTokenService.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnError_WhenRefreshTokenExpired() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("expired-token");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");

        RefreshTokenEntity expiredToken = new RefreshTokenEntity();
        expiredToken.setToken("expired-token");
        expiredToken.setUser(user);
        expiredToken.setExpiryDate(Instant.now().minusSeconds(1000));

        Mockito.when(refreshTokenService.findByToken("expired-token"))
                .thenReturn(Optional.of(expiredToken));
        Mockito.when(refreshTokenService.verifyExpiration(expiredToken))
                .thenThrow(new RefreshTokenException("Refresh token expired. Please login again."));

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldBeAccessibleWithoutAuthentication_Refresh() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("some-token");

        Mockito.when(refreshTokenService.findByToken("some-token"))
                .thenReturn(Optional.empty());

        // Should not return 401 due to missing auth, but due to invalid token
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ============ LOGOUT TESTS ============

    @Test
    @WithMockUser
    public void shouldLogout_WhenAuthenticated() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");

        Mockito.when(currentUserService.getCurrentUser()).thenReturn(user);
        Mockito.doNothing().when(refreshTokenService).deleteByUserId(1L);

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));

        Mockito.verify(refreshTokenService, Mockito.times(1)).deleteByUserId(1L);
    }

    @Test
    public void shouldReturnUnauthorized_WhenLogoutWithoutAuth() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isUnauthorized());
    }
}
