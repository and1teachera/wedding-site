package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private Authentication authentication;

    @Test
    @DisplayName("should extract family id from authentication")
    void shouldExtractFamilyIdFromAuthentication() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String username = firstName + " " + lastName;
        Long expectedFamilyId = 42L;

        when(authentication.getName()).thenReturn(username);
        when(jwtTokenProvider.getFamilyIdFromUsername(firstName, lastName)).thenReturn(expectedFamilyId);

        // Act
        Long result = tokenService.getFamilyIdFromAuthentication(authentication);

        // Assert
        assertEquals(expectedFamilyId, result);
        verify(jwtTokenProvider).getFamilyIdFromUsername(firstName, lastName);
    }

    @Test
    @DisplayName("should extract user id from authentication")
    void shouldExtractUserIdFromAuthentication() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String username = firstName + " " + lastName;
        Long expectedUserId = 123L;
        User user = User.builder().id(expectedUserId).build();

        when(authentication.getName()).thenReturn(username);
        when(userService.getByFirstNameAndLastName(firstName, lastName)).thenReturn(user);

        // Act
        Long result = tokenService.getUserIdFromAuthentication(authentication);

        // Assert
        assertEquals(expectedUserId, result);
        verify(userService).getByFirstNameAndLastName(firstName, lastName);
    }

    @Test
    @DisplayName("should throw exception when authentication has invalid format")
    void shouldThrowExceptionWhenAuthenticationHasInvalidFormat() {
        // Arrange
        String invalidUsername = "InvalidUsername";
        when(authentication.getName()).thenReturn(invalidUsername);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                tokenService.getFamilyIdFromAuthentication(authentication)
        );
    }

    @Test
    @DisplayName("should throw exception when authentication is null")
    void shouldThrowExceptionWhenAuthenticationIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                tokenService.getFamilyIdFromAuthentication(null)
        );
    }

    @Test
    @DisplayName("should handle authentication with three name parts")
    void shouldHandleAuthenticationWithThreeNameParts() {
        // Arrange
        String multiPartName = "John Middle Doe";
        when(authentication.getName()).thenReturn(multiPartName);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                tokenService.getFamilyIdFromAuthentication(authentication)
        );
    }
}