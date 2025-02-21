package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.RoomBookingRequest;
import com.zlatenov.wedding_backend.dto.RoomBookingResponse;
import com.zlatenov.wedding_backend.exception.ResourceNotFoundException;
import com.zlatenov.wedding_backend.exception.RsvpValidationException;
import com.zlatenov.wedding_backend.model.BookingStatus;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.ResponseStatus;
import com.zlatenov.wedding_backend.model.Room;
import com.zlatenov.wedding_backend.model.RoomBooking;
import com.zlatenov.wedding_backend.model.UserResponse;
import com.zlatenov.wedding_backend.repository.FamilyRepository;
import com.zlatenov.wedding_backend.repository.RoomBookingRepository;
import com.zlatenov.wedding_backend.repository.RoomRepository;
import com.zlatenov.wedding_backend.repository.UserResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * @author Angel Zlatenov
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomBookingRepository roomBookingRepository;
    private final FamilyRepository familyRepository;
    private final UserResponseRepository userResponseRepository;

    /**
     * Get the count of available rooms
     * @return The number of available rooms
     */
    @Transactional(readOnly = true)
    public int getAvailableRoomsCount() {
        return roomRepository.findByIsAvailableTrue().size();
    }

    /**
     * Book a room for a family
     * @param familyId The family ID
     * @param request The booking request details
     * @return Booking response with status
     */
    @Transactional
    public RoomBookingResponse bookRoom(Long familyId, RoomBookingRequest request) {
        // Verify family exists
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found"));

        // Verify at least one family member has RSVP'd 'YES'
        boolean anyAttending = family.getMembers().stream()
                .anyMatch(member -> {
                    Optional<UserResponse> response = userResponseRepository.findByUserId(member.getId());
                    return response.isPresent() && response.get().getStatus() == ResponseStatus.YES;
                });

        if (!anyAttending) {
            throw new RsvpValidationException("At least one family member must RSVP before booking a room");
        }

        // Find an available room
        Room availableRoom = roomRepository.findByIsAvailableTrue().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No rooms available"));

        // Create booking
        RoomBooking booking = RoomBooking.builder()
                .room(availableRoom)
                .family(family)
                .checkInDate(LocalDate.of(2025, 8, 8)) // Day before wedding
                .checkOutDate(LocalDate.of(2025, 8, 10)) // Day after wedding
                .status(BookingStatus.CONFIRMED)
                .notes(request.getNotes())
                .bookingTime(LocalDateTime.now())
                .build();

        roomBookingRepository.save(booking);

        // Update room availability
        availableRoom.setAvailable(false);
        roomRepository.save(availableRoom);

        log.info("Room {} booked for family ID: {}", availableRoom.getRoomNumber(), familyId);

        return RoomBookingResponse.builder()
                .success(true)
                .message("Room booked successfully")
                .roomNumber(availableRoom.getRoomNumber())
                .familyId(familyId)
                .build();
    }

    /**
     * Check if a family has a room booking
     * @param familyId The family ID
     * @return True if the family has a booking
     */
    @Transactional(readOnly = true)
    public boolean hasFamilyBookedRoom(Long familyId) {
        List<RoomBooking> bookings = roomBookingRepository.findByFamilyId(familyId);
        return !bookings.isEmpty();
    }

    /**
     * Get room booking details for a family
     * @param familyId The family ID
     * @return Room booking details or null if none exists
     */
    @Transactional(readOnly = true)
    public RoomBookingResponse getRoomBookingDetails(Long familyId) {
        List<RoomBooking> bookings = roomBookingRepository.findByFamilyId(familyId);

        if (bookings.isEmpty()) {
            return RoomBookingResponse.builder()
                    .success(false)
                    .message("No room booking found")
                    .build();
        }

        RoomBooking booking = bookings.get(0); // Get first booking

        return RoomBookingResponse.builder()
                .success(true)
                .message("Room booking found")
                .roomNumber(booking.getRoom().getRoomNumber())
                .familyId(familyId)
                .status(booking.getStatus().toString())
                .notes(booking.getNotes())
                .build();
    }

    /**
     * Cancel a room booking for a family
     * @param familyId The family ID
     * @return Cancellation response
     */
    @Transactional
    public RoomBookingResponse cancelRoomBooking(Long familyId) {
        List<RoomBooking> bookings = roomBookingRepository.findByFamilyId(familyId);

        if (bookings.isEmpty()) {
            return RoomBookingResponse.builder()
                    .success(false)
                    .message("No room booking found to cancel")
                    .build();
        }

        RoomBooking booking = bookings.get(0); // Get first booking
        Room room = booking.getRoom();

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        roomBookingRepository.save(booking);

        // Make room available again
        room.setAvailable(true);
        roomRepository.save(room);

        log.info("Room booking cancelled for family ID: {}, room number: {}",
                familyId, room.getRoomNumber());

        return RoomBookingResponse.builder()
                .success(true)
                .message("Room booking cancelled successfully")
                .build();
    }

}