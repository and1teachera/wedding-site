package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.Room;
import com.zlatenov.wedding_backend.model.UserGroup;
import com.zlatenov.wedding_backend.model.WaitingList;
import com.zlatenov.wedding_backend.repository.FamilyRepository;
import com.zlatenov.wedding_backend.repository.RoomRepository;
import com.zlatenov.wedding_backend.repository.UserGroupRepository;
import com.zlatenov.wedding_backend.repository.WaitingListRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaitingListServiceTest {

    @Mock
    private WaitingListRepository waitingListRepository;

    @Mock
    private FamilyRepository familyRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private WaitingListService waitingListService;

    @Captor
    private ArgumentCaptor<WaitingList> waitingListCaptor;

    @Test
    @DisplayName("should add to waiting list when no rooms available")
    void shouldAddToWaitingListWhenNoRoomsAvailable() {
        // Arrange
        Long familyId = 1L;
        String notes = "Test notes";
        Family family = Family.builder().id(familyId).name("Test Family").build();

        when(roomRepository.findByIsAvailableTrue()).thenReturn(Collections.emptyList());
        when(familyRepository.findById(familyId)).thenReturn(Optional.of(family));
        when(waitingListRepository.save(any(WaitingList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = waitingListService.addToWaitingListIfNeeded(familyId, null, notes);

        // Assert
        assertTrue(result);
        verify(waitingListRepository).save(waitingListCaptor.capture());
        WaitingList capturedWaitingList = waitingListCaptor.getValue();

        assertNotNull(capturedWaitingList.getRequestDate());
        assertFalse(capturedWaitingList.isNotificationSent());
        assertEquals(notes, capturedWaitingList.getNotes());
        assertEquals(family, capturedWaitingList.getFamily());
        assertNull(capturedWaitingList.getGroup());
    }

    @Test
    @DisplayName("should not add to waiting list when rooms are available")
    void shouldNotAddToWaitingListWhenRoomsAreAvailable() {
        // Arrange
        Long familyId = 1L;
        String notes = "Test notes";
        Room availableRoom = Room.builder().id(1L).roomNumber(101).isAvailable(true).build();

        when(roomRepository.findByIsAvailableTrue()).thenReturn(List.of(availableRoom));

        // Act
        boolean result = waitingListService.addToWaitingListIfNeeded(familyId, null, notes);

        // Assert
        assertFalse(result);
        verify(waitingListRepository, never()).save(any());
    }

    @Test
    @DisplayName("should add group to waiting list when no rooms available")
    void shouldAddGroupToWaitingListWhenNoRoomsAvailable() {
        // Arrange
        Long groupId = 1L;
        String notes = "Group notes";
        UserGroup group = UserGroup.builder().id(groupId).groupName("Test Group").build();

        when(roomRepository.findByIsAvailableTrue()).thenReturn(Collections.emptyList());
        when(userGroupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(waitingListRepository.save(any(WaitingList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = waitingListService.addToWaitingListIfNeeded(null, groupId, notes);

        // Assert
        assertTrue(result);
        verify(waitingListRepository).save(waitingListCaptor.capture());
        WaitingList capturedWaitingList = waitingListCaptor.getValue();

        assertNotNull(capturedWaitingList.getRequestDate());
        assertFalse(capturedWaitingList.isNotificationSent());
        assertEquals(notes, capturedWaitingList.getNotes());
        assertNull(capturedWaitingList.getFamily());
        assertEquals(group, capturedWaitingList.getGroup());
    }

    @Test
    @DisplayName("should throw exception when neither family nor group provided")
    void shouldThrowExceptionWhenNeitherFamilyNorGroupProvided() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                waitingListService.addToWaitingListIfNeeded(null, null, "Test notes")
        );

        verify(waitingListRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw exception when both family and group provided")
    void shouldThrowExceptionWhenBothFamilyAndGroupProvided() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                waitingListService.addToWaitingListIfNeeded(1L, 1L, "Test notes")
        );

        verify(waitingListRepository, never()).save(any());
    }

    @Test
    @DisplayName("should handle non-existent family when adding to waiting list")
    void shouldHandleNonExistentFamilyWhenAddingToWaitingList() {
        // Arrange
        Long nonExistentFamilyId = 999L;
        String notes = "Test notes";

        when(roomRepository.findByIsAvailableTrue()).thenReturn(Collections.emptyList());
        when(familyRepository.findById(nonExistentFamilyId)).thenReturn(Optional.empty());
        when(waitingListRepository.save(any(WaitingList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = waitingListService.addToWaitingListIfNeeded(nonExistentFamilyId, null, notes);

        // Assert
        assertTrue(result);
        verify(waitingListRepository).save(waitingListCaptor.capture());
        WaitingList capturedWaitingList = waitingListCaptor.getValue();

        assertNotNull(capturedWaitingList.getRequestDate());
        assertFalse(capturedWaitingList.isNotificationSent());
        assertEquals(notes, capturedWaitingList.getNotes());
        assertNull(capturedWaitingList.getFamily()); // Family should be null since it wasn't found
        assertNull(capturedWaitingList.getGroup());
    }
}