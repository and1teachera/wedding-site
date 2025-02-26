package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.FamilyMembersResponse;
import com.zlatenov.wedding_backend.dto.GuestResponse;
import com.zlatenov.wedding_backend.dto.RsvpRequest;
import com.zlatenov.wedding_backend.dto.RsvpResponse;
import com.zlatenov.wedding_backend.exception.UnauthorizedAccessException;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.ResponseStatus;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.model.UserResponse;
import com.zlatenov.wedding_backend.repository.UserResponseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RsvpServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserResponseRepository userResponseRepository;

    @Mock
    private WaitingListService waitingListService;

    @InjectMocks
    private RsvpServiceImpl rsvpService;

    @Test
    @DisplayName("Should process RSVP for primary guest")
    void shouldProcessRsvpForPrimaryGuest() {
        // Arrange
        String username = "John Doe";
        Long userId = 1L;

        User primaryUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .build();

        GuestResponse guestResponse = new GuestResponse();
        guestResponse.setUserId(userId);
        guestResponse.setStatus(ResponseStatus.YES);

        RsvpRequest request = new RsvpRequest();
        request.setPrimaryGuest(guestResponse);

        when(userService.getByFirstNameAndLastName("John", "Doe")).thenReturn(primaryUser);
        when(userService.getUserById(userId)).thenReturn(primaryUser);
        when(userResponseRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        RsvpResponse response = rsvpService.processRsvp(request, username);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(userId, response.getPrimaryUserId());
        assertEquals(1, response.getConfirmedAttendees());
        verify(userService).getByFirstNameAndLastName("John", "Doe");
        verify(userResponseRepository).save(any(UserResponse.class));
    }

    @Test
    @DisplayName("Should process RSVP for primary guest and family members")
    void shouldProcessRsvpForPrimaryGuestAndFamilyMembers() {
        // Arrange
        String username = "John Doe";
        Long primaryUserId = 1L;
        Long familyId = 10L;

        Family family = Family.builder()
                .id(familyId)
                .name("Doe Family")
                .build();

        User primaryUser = User.builder()
                .id(primaryUserId)
                .firstName("John")
                .lastName("Doe")
                .family(family)
                .build();

        User familyMember1 = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .family(family)
                .build();

        User familyMember2 = User.builder()
                .id(3L)
                .firstName("Junior")
                .lastName("Doe")
                .family(family)
                .isChild(true)
                .build();

        GuestResponse primaryGuestResponse = new GuestResponse();
        primaryGuestResponse.setUserId(primaryUserId);
        primaryGuestResponse.setStatus(ResponseStatus.YES);

        GuestResponse member1Response = new GuestResponse();
        member1Response.setUserId(2L);
        member1Response.setStatus(ResponseStatus.YES);

        GuestResponse member2Response = new GuestResponse();
        member2Response.setUserId(3L);
        member2Response.setStatus(ResponseStatus.NO);

        RsvpRequest request = new RsvpRequest();
        request.setPrimaryGuest(primaryGuestResponse);
        request.setFamilyMembers(Arrays.asList(member1Response, member2Response));

        when(userService.getByFirstNameAndLastName("John", "Doe")).thenReturn(primaryUser);
        when(userService.getUserById(primaryUserId)).thenReturn(primaryUser);
        when(userService.getUserById(2L)).thenReturn(familyMember1);
        when(userService.getUserById(3L)).thenReturn(familyMember2);
        when(userService.getFamilyMembers(familyId)).thenReturn(Arrays.asList(primaryUser, familyMember1, familyMember2));

        // Act
        RsvpResponse response = rsvpService.processRsvp(request, username);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(primaryUserId, response.getPrimaryUserId());
        assertEquals(2, response.getConfirmedAttendees()); // Primary + member1, member2 is NO
        verify(userService).getByFirstNameAndLastName("John", "Doe");
        verify(userResponseRepository, times(3)).save(any(UserResponse.class));
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when primary user ID doesn't match")
    void shouldThrowExceptionWhenPrimaryUserIdDoesntMatch() {
        // Arrange
        String username = "John Doe";
        Long primaryUserId = 1L;

        User primaryUser = User.builder()
                .id(primaryUserId)
                .firstName("John")
                .lastName("Doe")
                .build();

        GuestResponse guestResponse = new GuestResponse();
        guestResponse.setUserId(999L); // Different ID
        guestResponse.setStatus(ResponseStatus.YES);

        RsvpRequest request = new RsvpRequest();
        request.setPrimaryGuest(guestResponse);

        when(userService.getByFirstNameAndLastName("John", "Doe")).thenReturn(primaryUser);

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () ->
                rsvpService.processRsvp(request, username));

        assertEquals("Unauthorized access to user data", exception.getMessage());
        verify(userService).getByFirstNameAndLastName("John", "Doe");
        verify(userResponseRepository, never()).save(any(UserResponse.class));
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when family member is not in family")
    void shouldThrowExceptionWhenFamilyMemberNotInFamily() {
        // Arrange
        String username = "John Doe";
        Long primaryUserId = 1L;
        Long familyId = 10L;

        Family family = Family.builder()
                .id(familyId)
                .name("Doe Family")
                .build();

        User primaryUser = User.builder()
                .id(primaryUserId)
                .firstName("John")
                .lastName("Doe")
                .family(family)
                .build();

        User familyMember = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .family(family)
                .build();

        GuestResponse primaryGuestResponse = new GuestResponse();
        primaryGuestResponse.setUserId(primaryUserId);
        primaryGuestResponse.setStatus(ResponseStatus.YES);

        GuestResponse unauthorizedMemberResponse = new GuestResponse();
        unauthorizedMemberResponse.setUserId(999L); // Not in family
        unauthorizedMemberResponse.setStatus(ResponseStatus.YES);

        RsvpRequest request = new RsvpRequest();
        request.setPrimaryGuest(primaryGuestResponse);
        request.setFamilyMembers(Collections.singletonList(unauthorizedMemberResponse));

        when(userService.getByFirstNameAndLastName("John", "Doe")).thenReturn(primaryUser);
        when(userService.getUserById(primaryUserId)).thenReturn(primaryUser);
        when(userService.getFamilyMembers(familyId)).thenReturn(Arrays.asList(primaryUser, familyMember));

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () ->
                rsvpService.processRsvp(request, username));

        assertEquals("Unauthorized access to family member data", exception.getMessage());
    }

    @Test
    @DisplayName("Should get family members for RSVP form")
    void shouldGetFamilyMembers() {
        // Arrange
        String username = "John Doe";
        Long primaryUserId = 1L;
        Long familyId = 10L;

        Family family = Family.builder()
                .id(familyId)
                .name("Doe Family")
                .build();

        User primaryUser = User.builder()
                .id(primaryUserId)
                .firstName("John")
                .lastName("Doe")
                .family(family)
                .build();

        User familyMember = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .family(family)
                .build();

        UserResponse primaryResponse = UserResponse.builder()
                .id(1L)
                .user(primaryUser)
                .status(ResponseStatus.YES)
                .build();

        UserResponse memberResponse = UserResponse.builder()
                .id(2L)
                .user(familyMember)
                .status(ResponseStatus.MAYBE)
                .build();

        when(userService.getByFirstNameAndLastName("John", "Doe")).thenReturn(primaryUser);
        when(userService.getFamilyMembers(familyId)).thenReturn(Arrays.asList(primaryUser, familyMember));
        when(userResponseRepository.findByUserId(primaryUserId)).thenReturn(Optional.of(primaryResponse));
        when(userResponseRepository.findByUserId(2L)).thenReturn(Optional.of(memberResponse));

        // Act
        FamilyMembersResponse response = rsvpService.getFamilyMembers(username);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getPrimaryUser());
        assertEquals(primaryUserId, response.getPrimaryUser().getId());
        assertEquals(ResponseStatus.YES, response.getPrimaryUser().getRsvpStatus());

        assertNotNull(response.getFamilyMembers());
        assertEquals(1, response.getFamilyMembers().size());
        assertEquals(2L, response.getFamilyMembers().get(0).getId());
        assertEquals(ResponseStatus.MAYBE, response.getFamilyMembers().get(0).getRsvpStatus());
    }

    @Test
    @DisplayName("Should get RSVP status")
    void shouldGetRsvpStatus() {
        // Arrange
        String username = "John Doe";
        Long primaryUserId = 1L;
        Long familyId = 10L;

        Family family = Family.builder()
                .id(familyId)
                .name("Doe Family")
                .build();

        User primaryUser = User.builder()
                .id(primaryUserId)
                .firstName("John")
                .lastName("Doe")
                .family(family)
                .build();

        User familyMember = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .family(family)
                .build();

        UserResponse primaryResponse = UserResponse.builder()
                .id(1L)
                .user(primaryUser)
                .status(ResponseStatus.YES)
                .build();

        UserResponse memberResponse = UserResponse.builder()
                .id(2L)
                .user(familyMember)
                .status(ResponseStatus.YES)
                .build();

        when(userService.getByFirstNameAndLastName("John", "Doe")).thenReturn(primaryUser);
        when(userService.getFamilyMembers(familyId)).thenReturn(Arrays.asList(primaryUser, familyMember));
        when(userResponseRepository.findByUserId(primaryUserId)).thenReturn(Optional.of(primaryResponse));
        when(userResponseRepository.findByUserId(2L)).thenReturn(Optional.of(memberResponse));

        // Act
        RsvpResponse response = rsvpService.getRsvpStatus(username);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(primaryUserId, response.getPrimaryUserId());
        assertEquals(2, response.getConfirmedAttendees());
    }

    @Test
    @DisplayName("Should save primary guest response")
    void shouldSavePrimaryGuestResponse() {
        // Arrange
        String username = "John Doe";
        Long userId = 1L;

        User primaryUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .build();

        GuestResponse guestResponse = new GuestResponse();
        guestResponse.setUserId(userId);
        guestResponse.setStatus(ResponseStatus.YES);
        guestResponse.setDietaryNotes("Vegetarian");

        when(userService.getByFirstNameAndLastName("John", "Doe")).thenReturn(primaryUser);
        when(userResponseRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        RsvpResponse response = rsvpService.savePrimaryGuestResponse(guestResponse, username);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(userId, response.getPrimaryUserId());
        assertEquals(1, response.getConfirmedAttendees());
        verify(userService).getByFirstNameAndLastName("John", "Doe");
        verify(userResponseRepository).save(any(UserResponse.class));
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when saving response for different user")
    void shouldThrowExceptionWhenSavingResponseForDifferentUser() {
        // Arrange
        String username = "John Doe";
        Long userId = 1L;

        User primaryUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .build();

        GuestResponse guestResponse = new GuestResponse();
        guestResponse.setUserId(999L); // Different ID
        guestResponse.setStatus(ResponseStatus.YES);

        when(userService.getByFirstNameAndLastName("John", "Doe")).thenReturn(primaryUser);

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () ->
                rsvpService.savePrimaryGuestResponse(guestResponse, username));

        assertEquals("Unauthorized access to user data", exception.getMessage());
        verify(userService).getByFirstNameAndLastName("John", "Doe");
        verify(userResponseRepository, never()).save(any(UserResponse.class));
    }
}