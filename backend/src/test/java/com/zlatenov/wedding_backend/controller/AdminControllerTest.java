package com.zlatenov.wedding_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zlatenov.wedding_backend.configuration.TestSecurityConfiguration;
import com.zlatenov.wedding_backend.dto.FamilyCreationRequest;
import com.zlatenov.wedding_backend.dto.UserCreationRequest;
import com.zlatenov.wedding_backend.security.JwtTokenProvider;
import com.zlatenov.wedding_backend.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfiguration.class)
class AdminControllerTest {

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should create user successfully when admin")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUserSuccessfullyWhenAdmin() throws Exception {
        // Arrange
        UserCreationRequest request = UserCreationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .build();

        doNothing().when(adminService).createUser(any(UserCreationRequest.class));

        // Act & Assert
        mockMvc.perform(post("/admin/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(adminService, times(1)).createUser(any(UserCreationRequest.class));
    }

    @Test
    @DisplayName("Should return 403 when non-admin tries to create user")
    @WithMockUser(roles = "GUEST")
    void shouldReturn403WhenNonAdminTriesToCreateUser() throws Exception {
        // Arrange
        UserCreationRequest request = UserCreationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .build();

        // Act & Assert
        mockMvc.perform(post("/admin/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(adminService, times(0)).createUser(any(UserCreationRequest.class));
    }

    @Test
    @DisplayName("Should return 400 for invalid user creation request")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400ForInvalidUserCreationRequest() throws Exception {
        // Arrange - missing required fields
        UserCreationRequest request = UserCreationRequest.builder()
                .lastName("Doe") // Missing firstName
                .build();

        // Act & Assert
        mockMvc.perform(post("/admin/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(adminService, times(0)).createUser(any(UserCreationRequest.class));
    }

    @Test
    @DisplayName("Should create family successfully when admin")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateFamilySuccessfullyWhenAdmin() throws Exception {
        // Arrange
        UserCreationRequest primaryUser = UserCreationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        UserCreationRequest familyMember = UserCreationRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .build();

        FamilyCreationRequest request = FamilyCreationRequest.builder()
                .familyName("Doe Family")
                .primaryUser(primaryUser)
                .familyMembers(Collections.singletonList(familyMember))
                .build();

        doNothing().when(adminService).createFamily(any(FamilyCreationRequest.class));

        // Act & Assert
        mockMvc.perform(post("/admin/family")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(adminService, times(1)).createFamily(any(FamilyCreationRequest.class));
    }

    @Test
    @DisplayName("Should return 403 when non-admin tries to create family")
    @WithMockUser(roles = "GUEST")
    void shouldReturn403WhenNonAdminTriesToCreateFamily() throws Exception {
        // Arrange
        UserCreationRequest primaryUser = UserCreationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        // Create a family member to satisfy validation
        UserCreationRequest familyMember = UserCreationRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .build();

        FamilyCreationRequest request = FamilyCreationRequest.builder()
                .familyName("Doe Family")
                .primaryUser(primaryUser)
                .familyMembers(Collections.singletonList(familyMember))  // Add one family member
                .build();

        // Act & Assert
        mockMvc.perform(post("/admin/family")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(adminService, times(0)).createFamily(any(FamilyCreationRequest.class));
    }

    @Test
    @DisplayName("Should return 400 for invalid family creation request")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400ForInvalidFamilyCreationRequest() throws Exception {
        // Arrange - missing required fields
        FamilyCreationRequest request = FamilyCreationRequest.builder()
                // Missing primaryUser
                .familyName("Doe Family")
                .familyMembers(Collections.emptyList())
                .build();

        // Act & Assert
        mockMvc.perform(post("/admin/family")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(adminService, times(0)).createFamily(any(FamilyCreationRequest.class));
    }
}