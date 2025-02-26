package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.model.LoginAttempt;
import com.zlatenov.wedding_backend.repository.LoginAttemptRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginAuditServiceTest {

    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @InjectMocks
    private LoginAuditServiceImpl loginAuditService;

    @Captor
    private ArgumentCaptor<LoginAttempt> loginAttemptCaptor;

    @Test
    @DisplayName("should log successful login attempt")
    void shouldLogSuccessfulLoginAttempt() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";

        when(loginAttemptRepository.save(any(LoginAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        loginAuditService.logSuccessfulAttempt(firstName, lastName, ipAddress, userAgent);

        // Assert
        verify(loginAttemptRepository).save(loginAttemptCaptor.capture());
        LoginAttempt capturedAttempt = loginAttemptCaptor.getValue();

        assertNotNull(capturedAttempt.getAttemptTime());
        assertEquals(ipAddress, capturedAttempt.getIpAddress());
        assertTrue(capturedAttempt.isSuccessful());
        assertEquals(firstName + " " + lastName, capturedAttempt.getUsername());
        assertNull(capturedAttempt.getFailureReason());
        assertEquals(userAgent, capturedAttempt.getUserAgent());
    }

    @Test
    @DisplayName("should log failed login attempt")
    void shouldLogFailedLoginAttempt() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";

        when(loginAttemptRepository.save(any(LoginAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        loginAuditService.logFailedAttempt(firstName, lastName, ipAddress, userAgent);

        // Assert
        verify(loginAttemptRepository).save(loginAttemptCaptor.capture());
        LoginAttempt capturedAttempt = loginAttemptCaptor.getValue();

        assertNotNull(capturedAttempt.getAttemptTime());
        assertEquals(ipAddress, capturedAttempt.getIpAddress());
        assertFalse(capturedAttempt.isSuccessful());
        assertEquals(firstName + " " + lastName, capturedAttempt.getUsername());
        assertEquals("Invalid credentials", capturedAttempt.getFailureReason());
        assertEquals(userAgent, capturedAttempt.getUserAgent());
    }

    @Test
    @DisplayName("should create login attempt with correct structure")
    void shouldCreateLoginAttemptWithCorrectStructure() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";

        when(loginAttemptRepository.save(any(LoginAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        loginAuditService.logSuccessfulAttempt(firstName, lastName, ipAddress, userAgent);

        // Assert
        verify(loginAttemptRepository).save(loginAttemptCaptor.capture());
        LoginAttempt capturedAttempt = loginAttemptCaptor.getValue();

        assertNotNull(capturedAttempt);
        assertNotNull(capturedAttempt.getAttemptTime());
        assertNull(capturedAttempt.getId()); // ID should be null before save
        assertEquals(ipAddress, capturedAttempt.getIpAddress());
        assertEquals(userAgent, capturedAttempt.getUserAgent());
        assertEquals("John Doe", capturedAttempt.getUsername());
    }
}