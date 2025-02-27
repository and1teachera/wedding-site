package com.zlatenov.wedding_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zlatenov.wedding_backend.configuration.TestSecurityConfiguration;
import com.zlatenov.wedding_backend.dto.FamilyMembersResponse;
import com.zlatenov.wedding_backend.dto.GuestResponse;
import com.zlatenov.wedding_backend.dto.RsvpRequest;
import com.zlatenov.wedding_backend.dto.RsvpResponse;
import com.zlatenov.wedding_backend.dto.UserDto;
import com.zlatenov.wedding_backend.exception.UnauthorizedAccessException;
import com.zlatenov.wedding_backend.model.ResponseStatus;
import com.zlatenov.wedding_backend.security.JwtTokenProvider;
import com.zlatenov.wedding_backend.service.RsvpService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RsvpController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfiguration.class)
@WithMockUser
class RsvpControllerTest {

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RsvpService rsvpService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should submit RSVP successfully")
    void shouldSubmitRsvpSuccessfully() throws Exception {
        // Arrange
        GuestResponse primaryGuest = GuestResponse.builder()
                .userId(1L)
                .status(ResponseStatus.YES)
                .dietaryNotes("No peanuts")
                .build();

        GuestResponse familyMember = GuestResponse.builder()
                .userId(2L)
                .status(ResponseStatus.YES)
                .build();

        RsvpRequest request = RsvpRequest.builder()
                .primaryGuest(primaryGuest)
                .familyMembers(Collections.singletonList(familyMember))
                .build();

        RsvpResponse response = RsvpResponse.builder()
                .success(true)
                .message("RSVP has been successfully recorded")
                .primaryUserId(1L)
                .confirmedAttendees(2)
                .build();

        when(rsvpService.processRsvp(any(RsvpRequest.class), anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/rsvp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("RSVP has been successfully recorded"))
                .andExpect(jsonPath("$.primaryUserId").value(1))
                .andExpect(jsonPath("$.confirmedAttendees").value(2));
    }

    @Test
    @DisplayName("Should return 403 for unauthorized RSVP submissions")
    void shouldReturn403ForUnauthorizedRsvpSubmissions() throws Exception {
        // Arrange
        GuestResponse primaryGuest = GuestResponse.builder()
                .userId(999L) // Unauthorized user
                .status(ResponseStatus.YES)
                .build();

        RsvpRequest request = RsvpRequest.builder()
                .primaryGuest(primaryGuest)
                .familyMembers(Collections.emptyList())
                .build();

        when(rsvpService.processRsvp(any(RsvpRequest.class), anyString()))
                .thenThrow(new UnauthorizedAccessException("Unauthorized access to user data"));

        // Act & Assert
        mockMvc.perform(post("/rsvp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED_ACCESS"))
                .andExpect(jsonPath("$.message").value("Unauthorized access to user data"));
    }

    @Test
    @DisplayName("Should get family members successfully")
    void shouldGetFamilyMembersSuccessfully() throws Exception {
        // Arrange
        UserDto primaryUser = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .rsvpStatus(ResponseStatus.MAYBE)
                .build();

        UserDto familyMember = UserDto.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .isChild(false)
                .rsvpStatus(ResponseStatus.MAYBE)
                .build();

        FamilyMembersResponse response = FamilyMembersResponse.builder()
                .primaryUser(primaryUser)
                .familyMembers(Collections.singletonList(familyMember))
                .build();

        when(rsvpService.getFamilyMembers(anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/rsvp/family-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryUser.id").value(1))
                .andExpect(jsonPath("$.primaryUser.firstName").value("John"))
                .andExpect(jsonPath("$.primaryUser.lastName").value("Doe"))
                .andExpect(jsonPath("$.familyMembers[0].id").value(2))
                .andExpect(jsonPath("$.familyMembers[0].firstName").value("Jane"));
    }

    @Test
    @DisplayName("Should get RSVP status successfully")
    void shouldGetRsvpStatusSuccessfully() throws Exception {
        // Arrange
        RsvpResponse response = RsvpResponse.builder()
                .success(true)
                .message("Current RSVP status retrieved")
                .primaryUserId(1L)
                .confirmedAttendees(2)
                .build();

        when(rsvpService.getRsvpStatus(anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/rsvp/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Current RSVP status retrieved"))
                .andExpect(jsonPath("$.primaryUserId").value(1))
                .andExpect(jsonPath("$.confirmedAttendees").value(2));
    }

    @Test
    @DisplayName("Should save primary guest response successfully")
    void shouldSavePrimaryGuestResponseSuccessfully() throws Exception {
        // Arrange
        GuestResponse request = GuestResponse.builder()
                .userId(1L)
                .status(ResponseStatus.YES)
                .dietaryNotes("Vegetarian")
                .additionalNotes("Coming with family")
                .build();

        RsvpResponse response = RsvpResponse.builder()
                .success(true)
                .message("Primary guest response saved successfully")
                .primaryUserId(1L)
                .confirmedAttendees(1)
                .build();

        when(rsvpService.savePrimaryGuestResponse(any(GuestResponse.class), anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/rsvp/primary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Primary guest response saved successfully"))
                .andExpect(jsonPath("$.primaryUserId").value(1))
                .andExpect(jsonPath("$.confirmedAttendees").value(1));
    }

    @Test
    @DisplayName("Should return 403 for unauthorized primary guest response")
    void shouldReturn403ForUnauthorizedPrimaryGuestResponse() throws Exception {
        // Arrange
        GuestResponse request = GuestResponse.builder()
                .userId(999L) // Unauthorized user
                .status(ResponseStatus.YES)
                .build();

        when(rsvpService.savePrimaryGuestResponse(any(GuestResponse.class), anyString()))
                .thenThrow(new UnauthorizedAccessException("Unauthorized access to user data"));

        // Act & Assert
        mockMvc.perform(post("/rsvp/primary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED_ACCESS"))
                .andExpect(jsonPath("$.message").value("Unauthorized access to user data"));
    }

    @Test
    @DisplayName("Should return 400 for invalid guest response")
    void shouldReturn400ForInvalidGuestResponse() throws Exception {
        // Arrange - missing required fields
        GuestResponse request = GuestResponse.builder()
                .userId(1L)
                // Missing status
                .build();

        // Act & Assert
        mockMvc.perform(post("/rsvp/primary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}