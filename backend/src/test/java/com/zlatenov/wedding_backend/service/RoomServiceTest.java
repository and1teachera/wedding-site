package com.zlatenov.wedding_backend.service;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomBookingRepository roomBookingRepository;

    @Mock
    private FamilyService familyService;

    @Mock
    private UserService userService;

    @Mock
    private SingleUserAccommodationRepository singleUserAccommodationRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    @Captor
    private ArgumentCaptor<RoomBooking> roomBookingCaptor;

    @Captor
    private ArgumentCaptor<Room> roomCaptor;

    @Captor
    private ArgumentCaptor<SingleUserAccommodationRequest> accommodationRequestCaptor;

    @Test
    @DisplayName("should return correct count of available rooms")
    void shouldReturnCorrectCountOfAvailableRooms() {
        // Arrange
        List<Room> availableRooms = List.of(
                Room.builder().id(1L).roomNumber(101).isAvailable(true).build(),
                Room.builder().id(2L).roomNumber(102).isAvailable(true).build()
        );
        when(roomRepository.findByIsAvailableTrue()).thenReturn(availableRooms);

        // Act
        int count = roomService.getAvailableRoomsCount();

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("should book room successfully for family with attending members")
    void shouldBookRoomSuccessfullyForFamilyWithAttendingMembers() {
        // Arrange
        Long familyId = 1L;
        RoomBookingRequest request = RoomBookingRequest.builder()
                .notes("Test booking")
                .build();

        Family family = Family.builder().id(familyId).name("Test Family").build();
        Room availableRoom = Room.builder().id(1L).roomNumber(101).isAvailable(true).build();

        when(familyService.getFamilyById(familyId)).thenReturn(family);
        when(familyService.hasFamilyMembersAttending(family)).thenReturn(true);
        when(roomRepository.findFirstByIsAvailableTrue()).thenReturn(Optional.of(availableRoom));
        when(roomBookingRepository.save(any(RoomBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RoomBookingResponse response = roomService.bookRoom(familyId, request);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Room booked successfully", response.getMessage());
        assertEquals(availableRoom.getRoomNumber(), response.getRoomNumber());
        assertEquals(familyId, response.getFamilyId());

        verify(roomBookingRepository).save(roomBookingCaptor.capture());
        RoomBooking capturedBooking = roomBookingCaptor.getValue();

        assertEquals(availableRoom, capturedBooking.getRoom());
        assertEquals(family, capturedBooking.getFamily());
        assertEquals(BookingStatus.CONFIRMED, capturedBooking.getStatus());
        assertEquals(request.getNotes(), capturedBooking.getNotes());
        assertNotNull(capturedBooking.getBookingTime());

        verify(roomRepository).save(roomCaptor.capture());
        Room capturedRoom = roomCaptor.getValue();

        assertFalse(capturedRoom.isAvailable());
    }

    @Test
    @DisplayName("should throw exception when no attending members in family")
    void shouldThrowExceptionWhenNoAttendingMembersInFamily() {
        // Arrange
        Long familyId = 1L;
        RoomBookingRequest request = RoomBookingRequest.builder().build();

        Family family = Family.builder().id(familyId).name("Test Family").build();

        when(familyService.getFamilyById(familyId)).thenReturn(family);
        when(familyService.hasFamilyMembersAttending(family)).thenReturn(false);

        // Act & Assert
        assertThrows(RsvpValidationException.class, () ->
                roomService.bookRoom(familyId, request)
        );

        verify(roomBookingRepository, never()).save(any());
        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw exception when no rooms available")
    void shouldThrowExceptionWhenNoRoomsAvailable() {
        // Arrange
        Long familyId = 1L;
        RoomBookingRequest request = RoomBookingRequest.builder().build();

        Family family = Family.builder().id(familyId).name("Test Family").build();

        when(familyService.getFamilyById(familyId)).thenReturn(family);
        when(familyService.hasFamilyMembersAttending(family)).thenReturn(true);
        when(roomRepository.findFirstByIsAvailableTrue()).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                roomService.bookRoom(familyId, request)
        );

        verify(roomBookingRepository, never()).save(any());
        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("should get room booking details successfully")
    void shouldGetRoomBookingDetailsSuccessfully() {
        // Arrange
        Long familyId = 1L;
        Room room = Room.builder().id(1L).roomNumber(101).isAvailable(false).build();
        Family family = Family.builder().id(familyId).name("Test Family").build();

        RoomBooking booking = RoomBooking.builder()
                .id(1L)
                .room(room)
                .family(family)
                .status(BookingStatus.CONFIRMED)
                .notes("Test booking")
                .bookingTime(LocalDateTime.now())
                .build();

        when(roomBookingRepository.findLatestBookingByFamilyId(familyId)).thenReturn(Optional.of(booking));

        // Act
        RoomBookingResponse response = roomService.getRoomBookingDetails(familyId);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Room booking found", response.getMessage());
        assertEquals(room.getRoomNumber(), response.getRoomNumber());
        assertEquals(familyId, response.getFamilyId());
        assertEquals(booking.getStatus().toString(), response.getStatus());
        assertEquals(booking.getNotes(), response.getNotes());
    }

    @Test
    @DisplayName("should return appropriate response when no booking exists")
    void shouldReturnAppropriateResponseWhenNoBookingExists() {
        // Arrange
        Long familyId = 1L;
        when(roomBookingRepository.findLatestBookingByFamilyId(familyId)).thenReturn(Optional.empty());

        // Act
        RoomBookingResponse response = roomService.getRoomBookingDetails(familyId);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("No room booking found", response.getMessage());
        assertNull(response.getRoomNumber());
    }

    @Test
    @DisplayName("should request single accommodation successfully")
    void shouldRequestSingleAccommodationSuccessfully() {
        // Arrange
        Long userId = 1L;
        RoomBookingRequest request = RoomBookingRequest.builder()
                .notes("Single accommodation request")
                .build();

        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .family(null) // Single user, no family
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(singleUserAccommodationRepository.findLatestByUserId(userId)).thenReturn(Optional.empty());
        when(singleUserAccommodationRepository.save(any(SingleUserAccommodationRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RoomBookingResponse response = roomService.requestSingleAccommodation(userId, request);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Accommodation request submitted successfully", response.getMessage());
        assertEquals(SingleUserAccommodationStatus.PENDING.name(), response.getStatus());
        assertEquals(request.getNotes(), response.getNotes());

        verify(singleUserAccommodationRepository).save(accommodationRequestCaptor.capture());
        SingleUserAccommodationRequest capturedRequest = accommodationRequestCaptor.getValue();

        assertEquals(user, capturedRequest.getUser());
        assertEquals(SingleUserAccommodationStatus.PENDING, capturedRequest.getStatus());
        assertEquals(request.getNotes(), capturedRequest.getNotes());
    }

    @Test
    @DisplayName("should handle already pending accommodation requests")
    void shouldHandleAlreadyPendingAccommodationRequests() {
        // Arrange
        Long userId = 1L;
        RoomBookingRequest request = RoomBookingRequest.builder()
                .notes("New request")
                .build();

        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .family(null) // Single user
                .build();

        SingleUserAccommodationRequest existingRequest = SingleUserAccommodationRequest.builder()
                .id(1L)
                .user(user)
                .status(SingleUserAccommodationStatus.PENDING)
                .notes("Existing request")
                .requestDate(LocalDateTime.now().minusDays(1))
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(singleUserAccommodationRepository.findLatestByUserId(userId)).thenReturn(Optional.of(existingRequest));

        // Act
        RoomBookingResponse response = roomService.requestSingleAccommodation(userId, request);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("You already have a pending accommodation request", response.getMessage());
        assertEquals(SingleUserAccommodationStatus.PENDING.name(), response.getStatus());
        assertEquals(existingRequest.getNotes(), response.getNotes());

        // Verify no new request was saved
        verify(singleUserAccommodationRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw exception when family user requests single accommodation")
    void shouldThrowExceptionWhenFamilyUserRequestsSingleAccommodation() {
        // Arrange
        Long userId = 1L;
        RoomBookingRequest request = RoomBookingRequest.builder().build();

        Family family = Family.builder().id(1L).name("Test Family").build();
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .family(family) // User with family
                .build();

        when(userService.getUserById(userId)).thenReturn(user);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () ->
                roomService.requestSingleAccommodation(userId, request)
        );

        verify(singleUserAccommodationRepository, never()).save(any());
    }

    @Test
    @DisplayName("should get booking status for family user")
    void shouldGetBookingStatusForFamilyUser() {
        // Arrange
        Long userId = 1L;
        Long familyId = 5L;

        Family family = Family.builder().id(familyId).name("Test Family").build();
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .family(family)
                .build();

        Room room = Room.builder().id(1L).roomNumber(101).isAvailable(false).build();
        RoomBooking booking = RoomBooking.builder()
                .id(1L)
                .room(room)
                .family(family)
                .status(BookingStatus.CONFIRMED)
                .notes("Family booking")
                .bookingTime(LocalDateTime.now())
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(roomBookingRepository.findLatestBookingByFamilyId(familyId)).thenReturn(Optional.of(booking));

        // Act
        RoomBookingResponse response = roomService.getBookingStatusForUser(userId);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Room booking found", response.getMessage());
        assertEquals(room.getRoomNumber(), response.getRoomNumber());
        assertEquals(familyId, response.getFamilyId());
        assertEquals(booking.getStatus().toString(), response.getStatus());
    }

    @Test
    @DisplayName("should get booking status for single user")
    void shouldGetBookingStatusForSingleUser() {
        // Arrange
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .family(null) // No family
                .build();

        SingleUserAccommodationRequest request = SingleUserAccommodationRequest.builder()
                .id(1L)
                .user(user)
                .status(SingleUserAccommodationStatus.PENDING)
                .notes("Single user request")
                .requestDate(LocalDateTime.now())
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(singleUserAccommodationRepository.findLatestByUserId(userId)).thenReturn(Optional.of(request));

        // Act
        RoomBookingResponse response = roomService.getBookingStatusForUser(userId);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Single user accommodation request found", response.getMessage());
        assertEquals(SingleUserAccommodationStatus.PENDING.name(), response.getStatus());
        assertEquals(request.getNotes(), response.getNotes());
    }

    @Test
    @DisplayName("should cancel single accommodation request successfully")
    void shouldCancelSingleAccommodationRequestSuccessfully() {
        // Arrange
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .family(null) // No family
                .build();

        SingleUserAccommodationRequest pendingRequest = SingleUserAccommodationRequest.builder()
                .id(1L)
                .user(user)
                .status(SingleUserAccommodationStatus.PENDING)
                .notes("Pending request")
                .requestDate(LocalDateTime.now().minusDays(1))
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(singleUserAccommodationRepository.findLatestPendingByUserId(userId)).thenReturn(Optional.of(pendingRequest));
        when(singleUserAccommodationRepository.save(any(SingleUserAccommodationRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RoomBookingResponse response = roomService.cancelAccommodation(userId);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Accommodation request cancelled successfully", response.getMessage());

        verify(singleUserAccommodationRepository).save(accommodationRequestCaptor.capture());
        SingleUserAccommodationRequest capturedRequest = accommodationRequestCaptor.getValue();

        assertEquals(user, capturedRequest.getUser());
        assertEquals(SingleUserAccommodationStatus.CANCELLED, capturedRequest.getStatus());
        assertTrue(capturedRequest.getNotes().contains("Cancelled"));
    }

    @Test
    @DisplayName("should cancel family room booking successfully")
    void shouldCancelFamilyRoomBookingSuccessfully() {
        // Arrange
        Long userId = 1L;
        Long familyId = 5L;

        Family family = Family.builder().id(familyId).name("Test Family").build();
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .family(family)
                .build();

        Room room = Room.builder().id(1L).roomNumber(101).isAvailable(false).build();
        RoomBooking existingBooking = RoomBooking.builder()
                .id(1L)
                .room(room)
                .family(family)
                .status(BookingStatus.CONFIRMED)
                .notes("Existing booking")
                .bookingTime(LocalDateTime.now().minusDays(1))
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(roomBookingRepository.findLatestConfirmedBookingByFamilyId(familyId)).thenReturn(Optional.of(existingBooking));
        when(roomBookingRepository.save(any(RoomBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RoomBookingResponse response = roomService.cancelAccommodation(userId);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Room booking cancelled successfully", response.getMessage());

        verify(roomBookingRepository).save(roomBookingCaptor.capture());
        RoomBooking capturedBooking = roomBookingCaptor.getValue();

        assertEquals(room, capturedBooking.getRoom());
        assertEquals(family, capturedBooking.getFamily());
        assertEquals(BookingStatus.CANCELLED, capturedBooking.getStatus());
        assertTrue(capturedBooking.getNotes().contains("Cancelled"));

        verify(roomRepository).save(roomCaptor.capture());
        Room capturedRoom = roomCaptor.getValue();

        assertTrue(capturedRoom.isAvailable());
    }
}