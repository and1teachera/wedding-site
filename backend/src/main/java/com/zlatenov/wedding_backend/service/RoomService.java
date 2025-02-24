package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.RoomBookingRequest;
import com.zlatenov.wedding_backend.dto.RoomBookingResponse;
import com.zlatenov.wedding_backend.exception.ResourceNotFoundException;
import com.zlatenov.wedding_backend.exception.RsvpValidationException;
import com.zlatenov.wedding_backend.exception.UnauthorizedAccessException;
import com.zlatenov.wedding_backend.model.BookingStatus;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.ResponseStatus;
import com.zlatenov.wedding_backend.model.Room;
import com.zlatenov.wedding_backend.model.RoomBooking;
import com.zlatenov.wedding_backend.model.SingleUserAccommodationRequest;
import com.zlatenov.wedding_backend.model.SingleUserAccommodationStatus;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.model.UserResponse;
import com.zlatenov.wedding_backend.repository.FamilyRepository;
import com.zlatenov.wedding_backend.repository.RoomBookingRepository;
import com.zlatenov.wedding_backend.repository.RoomRepository;
import com.zlatenov.wedding_backend.repository.SingleUserAccommodationRepository;
import com.zlatenov.wedding_backend.repository.UserRepository;
import com.zlatenov.wedding_backend.repository.UserResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserRepository userRepository;
    private final SingleUserAccommodationRepository singleUserAccommodationRepository;

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
     * @return Room booking details if such exist
     */
    @Transactional(readOnly = true)
    public RoomBookingResponse getRoomBookingDetails(Long familyId) {
        Optional<RoomBooking> latestBookingOpt = roomBookingRepository.findLatestBookingByFamilyId(familyId);

        if (latestBookingOpt.isEmpty()) {
            return RoomBookingResponse.builder()
                    .success(false)
                    .message("No room booking found")
                    .build();
        }

        RoomBooking latestBooking = latestBookingOpt.get();

        // Check if the latest booking is CONFIRMED (active) or CANCELLED
        if (latestBooking.getStatus() == BookingStatus.CANCELLED) {
            return RoomBookingResponse.builder()
                    .success(false)
                    .message("No active room booking found")
                    .build();
        }

        // If we get here, the booking is confirmed and active
        return RoomBookingResponse.builder()
                .success(true)
                .message("Room booking found")
                .roomNumber(latestBooking.getRoom().getRoomNumber())
                .familyId(familyId)
                .status(latestBooking.getStatus().toString())
                .notes(latestBooking.getNotes())
                .build();
    }

    /**
     * Cancel a room booking for a family
     * @param familyId The family ID
     * @return Cancellation response
     */
    @Transactional
    public RoomBookingResponse cancelRoomBooking(Long familyId) {
        Optional<RoomBooking> latestConfirmedBooking = roomBookingRepository.findLatestConfirmedBookingByFamilyId(familyId);

        if (latestConfirmedBooking.isEmpty()) {
            return RoomBookingResponse.builder()
                    .success(false)
                    .message("No active room booking found to cancel")
                    .build();
        }

        RoomBooking booking = latestConfirmedBooking.get();
        Room room = booking.getRoom();

        RoomBooking cancelledBooking = RoomBooking.builder()
                .room(room)
                .family(booking.getFamily())
                .status(BookingStatus.CANCELLED)
                .notes("Cancelled booking: " + (booking.getNotes() != null ? booking.getNotes() : ""))
                .bookingTime(LocalDateTime.now())
                .build();

        roomBookingRepository.save(cancelledBooking);

        // Make room available again
        room.setAvailable(true);
        roomRepository.save(room);

        log.info("Room booking cancelled for family ID: {}, room number: {}, original booking ID: {}",
                familyId, room.getRoomNumber(), booking.getId());

        return RoomBookingResponse.builder()
                .success(true)
                .message("Room booking cancelled successfully")
                .build();
    }

    @Transactional
    public RoomBookingResponse requestSingleAccommodation(Long userId, RoomBookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getFamily() != null) {
            throw new UnauthorizedAccessException("Only single users can request single accommodation");
        }

        Optional<SingleUserAccommodationRequest> latestRequest =
                singleUserAccommodationRepository.findLatestByUserId(userId);

        if (latestRequest.isPresent() &&
                latestRequest.get().getStatus() == SingleUserAccommodationStatus.PENDING) {
            SingleUserAccommodationRequest pending = latestRequest.get();
            return RoomBookingResponse.builder()
                    .success(true)
                    .message("You already have a pending accommodation request")
                    .status(pending.getStatus().name())
                    .notes(pending.getNotes())
                    .build();
        }

        SingleUserAccommodationRequest accommodationRequest = SingleUserAccommodationRequest.builder()
                .user(user)
                .status(SingleUserAccommodationStatus.PENDING)
                .notes(request.getNotes())
                .build();

        singleUserAccommodationRepository.save(accommodationRequest);

        return RoomBookingResponse.builder()
                .success(true)
                .message("Accommodation request submitted successfully")
                .status(SingleUserAccommodationStatus.PENDING.name())
                .notes(request.getNotes())
                .build();
    }

    public RoomBookingResponse getBookingStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getFamily() == null) {
            Optional<SingleUserAccommodationRequest> latestRequest =
                    singleUserAccommodationRepository.findLatestByUserId(userId);

            if (latestRequest.isPresent()) {
                SingleUserAccommodationRequest request = latestRequest.get();
                return RoomBookingResponse.builder()
                        .success(request.getStatus() == SingleUserAccommodationStatus.PENDING)
                        .message("Single user accommodation request found")
                        .status(request.getStatus().name())
                        .notes(request.getNotes())
                        .build();
            }
            return RoomBookingResponse.builder()
                    .success(false)
                    .message("No accommodation request found")
                    .build();
        }

        return getRoomBookingDetails(user.getFamily().getId());
    }

    @Transactional
    public RoomBookingResponse cancelAccommodation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getFamily() == null) {
            Optional<SingleUserAccommodationRequest> latestPendingRequest =
                    singleUserAccommodationRepository.findLatestPendingByUserId(userId);

            if (latestPendingRequest.isPresent()) {
                SingleUserAccommodationRequest cancellation = SingleUserAccommodationRequest.builder()
                        .user(user)
                        .status(SingleUserAccommodationStatus.CANCELLED)
                        .notes("Cancelled: " + latestPendingRequest.get().getNotes())
                        .build();

                singleUserAccommodationRepository.save(cancellation);

                return RoomBookingResponse.builder()
                        .success(true)
                        .message("Accommodation request cancelled successfully")
                        .build();
            }
            return RoomBookingResponse.builder()
                    .success(false)
                    .message("No active accommodation request found")
                    .build();
        }

        return cancelRoomBooking(user.getFamily().getId());
    }
}