package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.FamilyCreationRequest;
import com.zlatenov.wedding_backend.dto.UserCreationRequest;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.repository.FamilyRepository;
import com.zlatenov.wedding_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FamilyRepository familyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Family> familyCaptor;

    private final String DEFAULT_PASSWORD = "defaultPassword";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adminService, "defaultPassword", DEFAULT_PASSWORD);
    }

    @Test
    @DisplayName("should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Arrange
        UserCreationRequest request = UserCreationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .isChild(false)
                .build();

        when(passwordEncoder.encode(DEFAULT_PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        adminService.createUser(request);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals(request.getFirstName(), capturedUser.getFirstName());
        assertEquals(request.getLastName(), capturedUser.getLastName());
        assertEquals(request.getEmail(), capturedUser.getEmail());
        assertEquals(request.getPhone(), capturedUser.getPhone());
        assertEquals(request.isChild(), capturedUser.isChild());
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertFalse(capturedUser.isAdmin());
        assertNull(capturedUser.getFamily());
    }

    @Test
    @DisplayName("should create family with primary user and members successfully")
    void shouldCreateFamilyWithPrimaryUserAndMembersSuccessfully() {
        // Arrange
        UserCreationRequest primaryUser = UserCreationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .isChild(false)
                .build();

        UserCreationRequest familyMember1 = UserCreationRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .isChild(false)
                .build();

        UserCreationRequest familyMember2 = UserCreationRequest.builder()
                .firstName("Junior")
                .lastName("Doe")
                .isChild(true)
                .build();

        FamilyCreationRequest request = FamilyCreationRequest.builder()
                .primaryUser(primaryUser)
                .familyName("Doe Family")
                .familyMembers(List.of(familyMember1, familyMember2))
                .build();

        Family savedFamily = Family.builder()
                .id(1L)
                .name(request.getFamilyName())
                .build();

        when(passwordEncoder.encode(DEFAULT_PASSWORD)).thenReturn("encodedPassword");
        when(familyRepository.save(any(Family.class))).thenReturn(savedFamily);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        adminService.createFamily(request);

        // Assert
        verify(familyRepository).save(familyCaptor.capture());
        Family capturedFamily = familyCaptor.getValue();
        assertEquals(request.getFamilyName(), capturedFamily.getName());

        verify(userRepository, times(3)).save(userCaptor.capture());
        List<User> capturedUsers = userCaptor.getAllValues();

        // Primary user
        User primaryUserCreated = capturedUsers.get(0);
        assertEquals(primaryUser.getFirstName(), primaryUserCreated.getFirstName());
        assertEquals(primaryUser.getLastName(), primaryUserCreated.getLastName());
        assertEquals(primaryUser.getEmail(), primaryUserCreated.getEmail());
        assertEquals("encodedPassword", primaryUserCreated.getPassword());
        assertEquals(savedFamily, primaryUserCreated.getFamily());

        // Family member 1
        User familyMember1Created = capturedUsers.get(1);
        assertEquals(familyMember1.getFirstName(), familyMember1Created.getFirstName());
        assertEquals(familyMember1.getLastName(), familyMember1Created.getLastName());
        assertEquals(familyMember1.getEmail(), familyMember1Created.getEmail());
        assertEquals("encodedPassword", familyMember1Created.getPassword());
        assertEquals(savedFamily, familyMember1Created.getFamily());
        assertFalse(familyMember1Created.isChild());

        // Family member 2
        User familyMember2Created = capturedUsers.get(2);
        assertEquals(familyMember2.getFirstName(), familyMember2Created.getFirstName());
        assertEquals(familyMember2.getLastName(), familyMember2Created.getLastName());
        assertEquals("encodedPassword", familyMember2Created.getPassword());
        assertEquals(savedFamily, familyMember2Created.getFamily());
        assertTrue(familyMember2Created.isChild());
    }

    @Test
    @DisplayName("should always encrypt password when creating users")
    void shouldAlwaysEncryptPasswordWhenCreatingUsers() {
        // Arrange
        UserCreationRequest request = UserCreationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        when(passwordEncoder.encode(DEFAULT_PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        adminService.createUser(request);

        // Assert
        verify(passwordEncoder).encode(DEFAULT_PASSWORD);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("encodedPassword", capturedUser.getPassword());
    }
}