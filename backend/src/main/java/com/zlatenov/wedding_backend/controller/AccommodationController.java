package com.zlatenov.wedding_backend.controller;

import com.zlatenov.wedding_backend.dto.RoomAvailabilityDto;
import com.zlatenov.wedding_backend.dto.RoomBookingRequest;
import com.zlatenov.wedding_backend.dto.RoomBookingResponse;
import com.zlatenov.wedding_backend.service.RoomService;
import com.zlatenov.wedding_backend.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Angel Zlatenov
 */

@Slf4j
@RestController
@RequestMapping("/accommodation")
@RequiredArgsConstructor
public class AccommodationController {

    private final RoomService roomService;
    private final TokenService tokenService;

    @GetMapping("/available-rooms")
    public ResponseEntity<Integer> getAvailableRoomsCount() {
        int availableRooms = roomService.getAvailableRoomsCount();
        return ResponseEntity.ok(availableRooms);
    }

    @PostMapping("/book")
    public ResponseEntity<RoomBookingResponse> bookRoom(
            @RequestBody @Valid RoomBookingRequest request,
            Authentication authentication) {

        Long familyId = tokenService.getFamilyIdFromAuthentication(authentication);

        RoomBookingResponse response = roomService.bookRoom(familyId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<RoomBookingResponse> cancelBooking(Authentication authentication) {
        Long userId = tokenService.getUserIdFromAuthentication(authentication);
        RoomBookingResponse response = roomService.cancelAccommodation(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booking-status")
    public ResponseEntity<RoomBookingResponse> getBookingStatus(Authentication authentication) {
        Long userId = tokenService.getUserIdFromAuthentication(authentication);
        RoomBookingResponse response = roomService.getBookingStatusForUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Request accommodation for a single user
     */
    @PostMapping("/request-single")
    public ResponseEntity<RoomBookingResponse> requestSingleAccommodation(@RequestBody @Valid RoomBookingRequest request,
            Authentication authentication) {

        Long userId = tokenService.getUserIdFromAuthentication(authentication);
        RoomBookingResponse response = roomService.requestSingleAccommodation(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomAvailabilityDto> getAllRoomsWithBookings() {
        log.info("Admin request to get all rooms with booking information");
        RoomAvailabilityDto response = roomService.getAllRoomsWithBookings();
        return ResponseEntity.ok(response);
    }

}
