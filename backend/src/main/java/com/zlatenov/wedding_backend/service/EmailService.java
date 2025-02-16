package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.exception.MailSendException;
import com.zlatenov.wedding_backend.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * @author Angel Zlatenov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
//
//    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("Please click the link below to reset your password: \n\n" +
                "http://localhost:4200/reset-password");

        try {
//            mailSender.send(message);
        } catch (MailException e) {
            log.error("Failed to send email to {}: {}", user.getEmail(), e.getMessage());
            throw new MailSendException("Failed to send password reset email");
        }
    }
}