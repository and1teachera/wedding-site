package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.exception.ResourceNotFoundException;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.ResponseStatus;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.model.UserResponse;
import com.zlatenov.wedding_backend.repository.FamilyRepository;
import com.zlatenov.wedding_backend.repository.UserResponseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FamilyServiceTest {

    @Mock
    private FamilyRepository familyRepository;

    @Mock
    private UserResponseRepository userResponseRepository;

    @InjectMocks
    private FamilyServiceImpl familyService;

    @Test
    @DisplayName("should get family by id when family exists")
    void shouldGetFamilyByIdWhenFamilyExists() {
        // Arrange
        Long familyId = 1L;
        Family expectedFamily = Family.builder()
                .id(familyId)
                .name("Test Family")
                .build();

        when(familyRepository.findById(familyId)).thenReturn(Optional.of(expectedFamily));

        // Act
        Family resultFamily = familyService.getFamilyById(familyId);

        // Assert
        assertNotNull(resultFamily);
        assertEquals(expectedFamily.getId(), resultFamily.getId());
        assertEquals(expectedFamily.getName(), resultFamily.getName());
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when family doesn't exist")
    void shouldThrowResourceNotFoundExceptionWhenFamilyDoesntExist() {
        // Arrange
        Long nonExistentFamilyId = 999L;
        when(familyRepository.findById(nonExistentFamilyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> familyService.getFamilyById(nonExistentFamilyId));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("should return true when at least one family member is attending")
    void shouldReturnTrueWhenAtLeastOneFamilyMemberIsAttending() {
        // Arrange
        User user1 = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        User user2 = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .build();

        Set<User> familyMembers = new HashSet<>();
        familyMembers.add(user1);
        familyMembers.add(user2);

        Family family = Family.builder()
                .id(1L)
                .name("Doe Family")
                .members(familyMembers)
                .build();

        UserResponse user1Response = UserResponse.builder()
                .id(1L)
                .user(user1)
                .status(ResponseStatus.YES)
                .build();

        UserResponse user2Response = UserResponse.builder()
                .id(2L)
                .user(user1)
                .status(ResponseStatus.NO)
                .build();

        when(userResponseRepository.findByUserId(1L)).thenReturn(Optional.of(user1Response));
        when(userResponseRepository.findByUserId(2L)).thenReturn(Optional.of(user2Response));
        // Act
        boolean result = familyService.hasFamilyMembersAttending(family);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("should return false when no family member is attending")
    void shouldReturnFalseWhenNoFamilyMemberIsAttending() {
        // Arrange
        User user1 = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        User user2 = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .build();

        Set<User> familyMembers = new HashSet<>();
        familyMembers.add(user1);
        familyMembers.add(user2);

        Family family = Family.builder()
                .id(1L)
                .name("Doe Family")
                .members(familyMembers)
                .build();

        UserResponse userResponse1 = UserResponse.builder()
                .id(1L)
                .user(user1)
                .status(ResponseStatus.NO)
                .build();

        UserResponse userResponse2 = UserResponse.builder()
                .id(2L)
                .user(user2)
                .status(ResponseStatus.MAYBE)
                .build();

        when(userResponseRepository.findByUserId(1L)).thenReturn(Optional.of(userResponse1));
        when(userResponseRepository.findByUserId(2L)).thenReturn(Optional.of(userResponse2));

        // Act
        boolean result = familyService.hasFamilyMembersAttending(family);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("should return false when family has no members")
    void shouldReturnFalseWhenFamilyHasNoMembers() {
        // Arrange
        Family family = Family.builder()
                .id(1L)
                .name("Empty Family")
                .members(new HashSet<>())
                .build();

        // Act
        boolean result = familyService.hasFamilyMembersAttending(family);

        // Assert
        assertFalse(result);
    }
}