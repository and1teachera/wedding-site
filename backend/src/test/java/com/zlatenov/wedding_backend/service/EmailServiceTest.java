package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.exception.MailSendException;
import com.zlatenov.wedding_backend.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("Should send password reset email successfully")
    void shouldSendPasswordResetEmail() {
        // Arrange
        User user = User.builder()
                .email("test@example.com")
                .build();
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail(user));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should throw MailSendException when email sending fails")
    void shouldThrowMailSendExceptionWhenSendingFails() {
        // Arrange
        User user = User.builder()
                .email("test@example.com")
                .build();
        doThrow(new org.springframework.mail.MailSendException("Failed to send email"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        MailSendException exception = assertThrows(MailSendException.class,
                () -> emailService.sendPasswordResetEmail(user));
        assertEquals("Failed to send password reset email", exception.getMessage());
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should validate email content")
    void shouldValidateEmailContent() {
        // Arrange
        User user = User.builder()
                .email("test@example.com")
                .build();

        emailService.sendPasswordResetEmail(user);

        // Assert
        verify(mailSender).send(argThat((SimpleMailMessage message) -> {
            assertEquals("test@example.com", Objects.requireNonNull(message.getTo())[0]);
            assertEquals("Password Reset Request", message.getSubject());
            assertTrue(Objects.requireNonNull(message.getText()).contains("reset your password"));
            return true;
        }));
    }
}