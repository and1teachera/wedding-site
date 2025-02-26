package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.exception.InvalidCredentialsException;
import com.zlatenov.wedding_backend.exception.UserNotFoundException;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should authenticate user with valid names and password")
    void shouldAuthenticateUserWithValidNames() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String password = "password123";
        String hashedPassword = "hashedPassword";
        String ipAddress = "127.0.0.1";
        String userAgent = "Test-Agent";

        User user = User.builder()
                .id(1L)
                .firstName(firstName)
                .lastName(lastName)
                .password(hashedPassword)
                .build();

        when(userRepository.findByFirstNameAndLastName(firstName, lastName))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

        // Act
        User result = userService.authenticateUserWithNames(firstName, lastName, password, ipAddress, userAgent);

        // Assert
        assertNotNull(result);
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        verify(userRepository).findByFirstNameAndLastName(firstName, lastName);
        verify(passwordEncoder).matches(password, hashedPassword);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when user not found by names")
    void shouldThrowExceptionWhenUserNotFoundByNames() {
        // Arrange
        String firstName = "NonExistent";
        String lastName = "User";
        String password = "password123";
        String ipAddress = "127.0.0.1";
        String userAgent = "Test-Agent";

        when(userRepository.findByFirstNameAndLastName(firstName, lastName))
                .thenReturn(Optional.empty());

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () ->
                userService.authenticateUserWithNames(firstName, lastName, password, ipAddress, userAgent));

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByFirstNameAndLastName(firstName, lastName);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password is incorrect")
    void shouldThrowExceptionWhenPasswordIncorrect() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String password = "wrongPassword";
        String hashedPassword = "hashedPassword";
        String ipAddress = "127.0.0.1";
        String userAgent = "Test-Agent";

        User user = User.builder()
                .id(1L)
                .firstName(firstName)
                .lastName(lastName)
                .password(hashedPassword)
                .build();

        when(userRepository.findByFirstNameAndLastName(firstName, lastName))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () ->
                userService.authenticateUserWithNames(firstName, lastName, password, ipAddress, userAgent));

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByFirstNameAndLastName(firstName, lastName);
        verify(passwordEncoder).matches(password, hashedPassword);
    }

    @Test
    @DisplayName("Should authenticate user with valid email and password")
    void shouldAuthenticateUserWithValidEmail() {
        // Arrange
        String email = "john.doe@example.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";
        String ipAddress = "127.0.0.1";
        String userAgent = "Test-Agent";

        User user = User.builder()
                .id(1L)
                .email(email)
                .password(hashedPassword)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

        // Act
        User result = userService.authenticateUserWithEmail(email, password, ipAddress, userAgent);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, hashedPassword);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when user not found by email")
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password123";
        String ipAddress = "127.0.0.1";
        String userAgent = "Test-Agent";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () ->
                userService.authenticateUserWithEmail(email, password, ipAddress, userAgent));

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should get user by Id")
    void shouldGetUserById() {
        // Arrange
        Long userId = 1L;
        User expectedUser = User.builder().id(userId).firstName("John").lastName("Doe").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found by Id")
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.getUserById(userId));

        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should get user by email")
    void shouldGetUserByEmail() {
        // Arrange
        String email = "john.doe@example.com";
        User expectedUser = User.builder().id(1L).email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.getByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should get user by first and last name")
    void shouldGetUserByFirstAndLastName() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        User expectedUser = User.builder().id(1L).firstName(firstName).lastName(lastName).build();

        when(userRepository.findByFirstNameAndLastName(firstName, lastName)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.getByFirstNameAndLastName(firstName, lastName);

        // Assert
        assertNotNull(result);
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        verify(userRepository).findByFirstNameAndLastName(firstName, lastName);
    }

    @Test
    @DisplayName("Should get family members by family Id")
    void shouldGetFamilyMembers() {
        // Arrange
        Long familyId = 1L;
        List<User> expectedMembers = Arrays.asList(
                User.builder().id(1L).firstName("John").lastName("Doe").build(),
                User.builder().id(2L).firstName("Jane").lastName("Doe").build()
        );

        when(userRepository.findByFamilyId(familyId)).thenReturn(expectedMembers);

        // Act
        List<User> result = userService.getFamilyMembers(familyId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findByFamilyId(familyId);
    }

    @Test
    @DisplayName("Should update user password")
    void shouldUpdatePassword() {
        // Arrange
        Long userId = 1L;
        String newPassword = "newPassword123";
        String hashedPassword = "hashedNewPassword";
        User user = User.builder().id(userId).password("oldHashedPassword").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn(hashedPassword);

        // Act
        userService.updatePassword(userId, newPassword);

        // Assert
        assertEquals(hashedPassword, user.getPassword());
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should not update password when user not found")
    void shouldNotUpdatePasswordWhenUserNotFound() {
        // Arrange
        Long userId = 999L;
        String newPassword = "newPassword123";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        userService.updatePassword(userId, newPassword);

        // Assert
        verify(userRepository).findById(userId);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}