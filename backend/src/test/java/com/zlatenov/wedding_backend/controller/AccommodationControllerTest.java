package com.zlatenov.wedding_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zlatenov.wedding_backend.configuration.TestSecurityConfiguration;
import com.zlatenov.wedding_backend.dto.RoomBookingRequest;
import com.zlatenov.wedding_backend.dto.RoomBookingResponse;
import com.zlatenov.wedding_backend.exception.ResourceNotFoundException;
import com.zlatenov.wedding_backend.exception.RsvpValidationException;
import com.zlatenov.wedding_backend.exception.UnauthorizedAccessException;
import com.zlatenov.wedding_backend.security.JwtTokenProvider;
import com.zlatenov.wedding_backend.service.RoomService;
import com.zlatenov.wedding_backend.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccommodationController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfiguration.class)
@WithMockUser
class AccommodationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private TokenService tokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should get available rooms count")
    void shouldGetAvailableRoomsCount() throws Exception {
        // Arrange
        when(roomService.getAvailableRoomsCount()).thenReturn(5);

        // Act & Assert
        mockMvc.perform(get("/accommodation/available-rooms"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @DisplayName("Should book room successfully")
    void shouldBookRoomSuccessfully() throws Exception {
        // Arrange
        RoomBookingRequest request = new RoomBookingRequest("Special requirements");
        RoomBookingResponse response = RoomBookingResponse.builder()
                .success(true)
                .message("Room booked successfully")
                .roomNumber(101)
                .familyId(1L)
                .status("CONFIRMED")
                .build();

        when(tokenService.getFamilyIdFromAuthentication(any(Authentication.class))).thenReturn(1L);
        when(roomService.bookRoom(eq(1L), any(RoomBookingRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/accommodation/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Room booked successfully"))
                .andExpect(jsonPath("$.roomNumber").value(101))
                .andExpect(jsonPath("$.familyId").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("Should return error when no rooms available")
    void shouldReturnErrorWhenNoRoomsAvailable() throws Exception {
        // Arrange
        RoomBookingRequest request = new RoomBookingRequest("Special requirements");

        when(tokenService.getFamilyIdFromAuthentication(any(Authentication.class))).thenReturn(1L);
        when(roomService.bookRoom(eq(1L), any(RoomBookingRequest.class)))
                .thenThrow(new ResourceNotFoundException("No rooms available"));

        // Act & Assert
        mockMvc.perform(post("/accommodation/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("No rooms available"));
    }

    @Test
    @DisplayName("Should return error when booking without RSVP")
    void shouldReturnErrorWhenBookingWithoutRsvp() throws Exception {
        // Arrange
        RoomBookingRequest request = new RoomBookingRequest("Special requirements");

        when(tokenService.getFamilyIdFromAuthentication(any(Authentication.class))).thenReturn(1L);
        when(roomService.bookRoom(eq(1L), any(RoomBookingRequest.class)))
                .thenThrow(new RsvpValidationException("At least one family member must RSVP before booking a room"));

        // Act & Assert
        mockMvc.perform(post("/accommodation/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("RSVP_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("At least one family member must RSVP before booking a room"));
    }

    @Test
    @DisplayName("Should cancel booking successfully")
    void shouldCancelBookingSuccessfully() throws Exception {
        // Arrange
        RoomBookingResponse response = RoomBookingResponse.builder()
                .success(true)
                .message("Room booking cancelled successfully")
                .build();

        when(tokenService.getUserIdFromAuthentication(any(Authentication.class))).thenReturn(1L);
        when(roomService.cancelAccommodation(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/accommodation/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Room booking cancelled successfully"));
    }

    @Test
    @DisplayName("Should get booking status successfully")
    void shouldGetBookingStatusSuccessfully() throws Exception {
        // Arrange
        RoomBookingResponse response = RoomBookingResponse.builder()
                .success(true)
                .message("Room booking found")
                .roomNumber(101)
                .familyId(1L)
                .status("CONFIRMED")
                .notes("Special requirements")
                .build();

        when(tokenService.getUserIdFromAuthentication(any(Authentication.class))).thenReturn(1L);
        when(roomService.getBookingStatusForUser(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/accommodation/booking-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Room booking found"))
                .andExpect(jsonPath("$.roomNumber").value(101))
                .andExpect(jsonPath("$.familyId").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.notes").value("Special requirements"));
    }

    @Test
    @DisplayName("Should request single accommodation successfully")
    void shouldRequestSingleAccommodationSuccessfully() throws Exception {
        // Arrange
        RoomBookingRequest request = new RoomBookingRequest("Single accommodation request");
        RoomBookingResponse response = RoomBookingResponse.builder()
                .success(true)
                .message("Accommodation request submitted successfully")
                .status("PENDING")
                .notes("Single accommodation request")
                .build();

        when(tokenService.getUserIdFromAuthentication(any(Authentication.class))).thenReturn(1L);
        when(roomService.requestSingleAccommodation(eq(1L), any(RoomBookingRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/accommodation/request-single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Accommodation request submitted successfully"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.notes").value("Single accommodation request"));
    }

    @Test
    @DisplayName("Should return error when family user requests single accommodation")
    void shouldReturnErrorWhenFamilyUserRequestsSingleAccommodation() throws Exception {
        // Arrange
        RoomBookingRequest request = new RoomBookingRequest("Single accommodation request");

        when(tokenService.getUserIdFromAuthentication(any(Authentication.class))).thenReturn(1L);
        when(roomService.requestSingleAccommodation(eq(1L), any(RoomBookingRequest.class)))
                .thenThrow(new UnauthorizedAccessException("Only single users can request single accommodation"));

        // Act & Assert
        mockMvc.perform(post("/accommodation/request-single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED_ACCESS"))
                .andExpect(jsonPath("$.message").value("Only single users can request single accommodation"));
    }
}