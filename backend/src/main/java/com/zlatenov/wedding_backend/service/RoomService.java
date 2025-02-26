package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.RoomBookingRequest;
import com.zlatenov.wedding_backend.dto.RoomBookingResponse;
import jakarta.validation.Valid;

/**
 * @author Angel Zlatenov
 */

public interface RoomService {

    /**
     * Get the count of available rooms
     * @return The number of available rooms
     */
    int getAvailableRoomsCount();

    /**
     * Book a room for a family
     * @param familyId The family Id
     * @param request The booking request details
     * @return Booking response with status
     */
    RoomBookingResponse bookRoom(Long familyId, RoomBookingRequest request);

    /**
     * Get room booking details for a family
     * @param familyId The family Id
     * @return Room booking details if such exist
     */
    RoomBookingResponse getRoomBookingDetails(Long familyId);

    RoomBookingResponse cancelAccommodation(Long userId);

    RoomBookingResponse getBookingStatusForUser(Long userId);

    RoomBookingResponse requestSingleAccommodation(Long userId, @Valid RoomBookingRequest request);
}
