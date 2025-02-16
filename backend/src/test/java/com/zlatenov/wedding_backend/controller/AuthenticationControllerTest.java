package com.zlatenov.wedding_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zlatenov.wedding_backend.configuration.TestSecurityConfiguration;
import com.zlatenov.wedding_backend.dto.LoginByEmailRequest;
import com.zlatenov.wedding_backend.dto.LoginByNamesRequest;
import com.zlatenov.wedding_backend.exception.InvalidCredentialsException;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.security.JwtTokenProvider;
import com.zlatenov.wedding_backend.service.LoginAuditService;
import com.zlatenov.wedding_backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfiguration.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private LoginAuditService loginAuditService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should authenticate successfully with email")
    void shouldAuthenticateWithEmail() throws Exception {
        // Arrange
        LoginByEmailRequest request = new LoginByEmailRequest("test@example.com", "password");

        when(userService.authenticateUser(any(), any(), any(), any()))
                .thenReturn(User.builder().firstName("Test").lastName("User").build());

        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("test.jwt.token");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test.jwt.token"))
                .andExpect(jsonPath("$.userType").value("GUEST"));
    }

    @Test
    @DisplayName("Should authenticate successfully with names")
    void shouldAuthenticateWithNames() throws Exception {
        // Arrange
        LoginByNamesRequest request = new LoginByNamesRequest("John", "Doe", "password");

        when(userService.authenticateUser(any(), any(), any(), any(), any()))
                .thenReturn(User.builder().firstName("Test").lastName("User").build());

        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("test.jwt.token");

        // Act & Assert
        mockMvc.perform(post("/auth/login-by-names")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test.jwt.token"))
                .andExpect(jsonPath("$.userType").value("GUEST"));
    }

    @Test
    @DisplayName("Should return 401 for invalid credentials")
    void shouldReturn401ForInvalidCredentials() throws Exception {
        // Arrange
        LoginByEmailRequest request = new LoginByEmailRequest("test@example.com", "wrongpassword");
        when(userService.authenticateUser(any(), any(), any(), any()))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 400 for invalid email format")
    void shouldReturn400ForInvalidEmailFormat() throws Exception {
        // Arrange
        LoginByEmailRequest request = new LoginByEmailRequest("invalid-email", "password");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for empty request body")
    void shouldReturn400ForEmptyRequestBody() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Request body is required"));
    }

    @Test
    @DisplayName("Should return proper error response for invalid credentials")
    void shouldReturnProperErrorResponseForInvalidCredentials() throws Exception {
        LoginByEmailRequest request = new LoginByEmailRequest("test@example.com", "wrongpassword");
        when(userService.authenticateUser(any(), any(), any(), any()))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").exists());
    }
}