package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.RoomAvailabilityDto;
import com.zlatenov.wedding_backend.dto.RoomBookingRequest;
import com.zlatenov.wedding_backend.dto.RoomBookingResponse;
import com.zlatenov.wedding_backend.exception.ResourceNotFoundException;
import com.zlatenov.wedding_backend.exception.RsvpValidationException;
import com.zlatenov.wedding_backend.exception.UnauthorizedAccessException;
import com.zlatenov.wedding_backend.model.BookingStatus;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.Room;
import com.zlatenov.wedding_backend.model.RoomBooking;
import com.zlatenov.wedding_backend.model.SingleUserAccommodationRequest;
import com.zlatenov.wedding_backend.model.SingleUserAccommodationStatus;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.repository.RoomBookingRepository;
import com.zlatenov.wedding_backend.repository.RoomRepository;
import com.zlatenov.wedding_backend.repository.SingleUserAccommodationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
/**
 * @author Angel Zlatenov
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final RoomBookingRepository roomBookingRepository;
    private final FamilyService familyService;
    private final UserService userService;
    private final SingleUserAccommodationRepository singleUserAccommodationRepository;

    @Override
    public int getAvailableRoomsCount() {
        return roomRepository.findByIsAvailableTrue().size();
    }

    @Transactional
    @Override
    public RoomBookingResponse bookRoom(Long familyId, RoomBookingRequest request) {
        Family family = familyService.getFamilyById(familyId);

        verifyFamilyMembersAttendance(family);

        Room availableRoom = roomRepository.findFirstByIsAvailableTrue()
                .orElseThrow(() -> new ResourceNotFoundException("No rooms available"));

        RoomBooking booking = RoomBooking.builder()
                .room(availableRoom)
                .family(family)
                .status(BookingStatus.CONFIRMED)
                .notes(request.getNotes())
                .bookingTime(LocalDateTime.now())
                .build();

        roomBookingRepository.save(booking);

        availableRoom.setAvailable(false);
        roomRepository.save(availableRoom);

        log.info("Room {} booked for family Id: {}", availableRoom.getRoomNumber(), familyId);

        return RoomBookingResponse.builder()
                .success(true)
                .message("Room booked successfully")
                .roomNumber(availableRoom.getRoomNumber())
                .familyId(familyId)
                .build();
    }

    @Override
    public RoomBookingResponse getRoomBookingDetails(Long familyId) {
        Optional<RoomBooking> latestBookingOpt = roomBookingRepository.findLatestBookingByFamilyId(familyId);

        if (latestBookingOpt.isEmpty()) {
            return RoomBookingResponse.builder()
                    .success(false)
                    .message("No room booking found")
                    .build();
        }

        RoomBooking latestBooking = latestBookingOpt.get();

        if (latestBooking.getStatus() == BookingStatus.CANCELLED) {
            return RoomBookingResponse.builder()
                    .success(false)
                    .message("No active room booking found")
                    .build();
        }

        return RoomBookingResponse.builder()
                .success(true)
                .message("Room booking found")
                .roomNumber(latestBooking.getRoom().getRoomNumber())
                .familyId(familyId)
                .status(latestBooking.getStatus().toString())
                .notes(latestBooking.getNotes())
                .build();
    }

    @Override
    @Transactional
    public RoomBookingResponse requestSingleAccommodation(Long userId, RoomBookingRequest request) {
        User user = userService.getUserById(userId);

        if (user.getFamily() != null) {
            throw new UnauthorizedAccessException("Only single users can request single accommodation");
        }

        Optional<SingleUserAccommodationRequest> latestRequest = singleUserAccommodationRepository.findLatestByUserId(userId);

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

    @Override
    public RoomBookingResponse getBookingStatusForUser(Long userId) {
        User user = userService.getUserById(userId);

        if (user.getFamily() != null) {
            return getRoomBookingDetails(user.getFamily().getId());
        }
        Optional<SingleUserAccommodationRequest> latestRequest = singleUserAccommodationRepository.findLatestByUserId(userId);

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

    @Transactional
    @Override
    public RoomBookingResponse cancelAccommodation(Long userId) {
        User user = userService.getUserById(userId);

        if (user.getFamily() != null) {
            return cancelRoomBooking(user.getFamily().getId());
        }
        Optional<SingleUserAccommodationRequest> latestPendingRequest = singleUserAccommodationRepository.findLatestPendingByUserId(userId);

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

    private void verifyFamilyMembersAttendance(Family family) {
        boolean anyAttending = familyService.hasFamilyMembersAttending(family);

        if (!anyAttending) {
            throw new RsvpValidationException("At least one family member must RSVP before booking a room");
        }
    }

    private RoomBookingResponse cancelRoomBooking(Long familyId) {
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

        log.info("Room booking cancelled for family Id: {}, room number: {}, original booking Id: {}",
                familyId, room.getRoomNumber(), booking.getId());

        return RoomBookingResponse.builder()
                .success(true)
                .message("Room booking cancelled successfully")
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public RoomAvailabilityDto getAllRoomsWithBookings() {
        // Fetch all rooms
        List<Room> allRooms = roomRepository.findAll();
        
        int availableRooms = 0;
        int bookedRooms = 0;
        
        // Create DTOs for each room with booking details
        List<RoomAvailabilityDto.RoomInfoDto> roomDtos = new ArrayList<>();
        
        for (Room room : allRooms) {
            // Count available vs. booked rooms
            if (room.isAvailable()) {
                availableRooms++;
            } else {
                bookedRooms++;
            }
            
            // Get booking information if the room is booked
            RoomAvailabilityDto.BookingInfoDto bookingInfo = null;
            if (!room.isAvailable()) {
                // Get the latest booking for this room
                Optional<RoomBooking> latestBooking = roomBookingRepository.findLatestBookingByRoomId(room.getId());
                
                if (latestBooking.isPresent()) {
                    RoomBooking booking = latestBooking.get();
                    String bookedBy = "";
                    Long familyId = null;
                    
                    // Determine who booked the room
                    if (booking.getFamily() != null) {
                        bookedBy = booking.getFamily().getName();
                        familyId = booking.getFamily().getId();
                    } else if (booking.getGroup() != null) {
                        bookedBy = "Group: " + booking.getGroup().getGroupName();
                        familyId = null;
                    }
                    
                    bookingInfo = RoomAvailabilityDto.BookingInfoDto.builder()
                            .bookingId(booking.getId())
                            .status(booking.getStatus().toString())
                            .bookingTime(booking.getBookingTime())
                            .bookedBy(bookedBy)
                            .familyId(familyId)
                            .notes(booking.getNotes())
                            .build();
                }
            }
            
            // Build the room DTO
            RoomAvailabilityDto.RoomInfoDto roomDto = RoomAvailabilityDto.RoomInfoDto.builder()
                    .roomId(room.getId())
                    .roomNumber(room.getRoomNumber())
                    .available(room.isAvailable())
                    .booking(bookingInfo)
                    .build();
            
            roomDtos.add(roomDto);
        }
        
        // Sort rooms by room number for easier display
        roomDtos.sort(Comparator.comparing(RoomAvailabilityDto.RoomInfoDto::getRoomNumber));
        
        log.info("Retrieved room availability. Total: {}, Available: {}, Booked: {}", 
                allRooms.size(), availableRooms, bookedRooms);
        
        // Build the response
        return RoomAvailabilityDto.builder()
                .rooms(roomDtos)
                .totalRooms(allRooms.size())
                .availableRooms(availableRooms)
                .bookedRooms(bookedRooms)
                .build();
    }
}