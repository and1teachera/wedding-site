package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.model.LoginAttempt;
import com.zlatenov.wedding_backend.repository.LoginAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginAuditService {
    private final LoginAttemptRepository loginAttemptRepository;

    /**
     * Logs a successful login attempt asynchronously to avoid impacting the login response time.
     * Uses a separate transaction to ensure logging succeeds even if the main transaction fails.
     *
     * @param firstName The firstname attempting to log in
     * @param lastName The lastname attempting to log in
     * @param ipAddress The IP address of the login attempt
     * @param userAgent The browser/client information
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSuccessfulAttempt(String firstName, String lastName, String ipAddress, String userAgent) {
        logAttempt(firstName, lastName, ipAddress, userAgent, true, null);
    }

    /**
     * Logs a failed login attempt asynchronously to avoid impacting the login response time.
     * Uses a separate transaction to ensure logging succeeds even if the main transaction fails.
     *
     * @param firstName The firstname attempting to log in
     * @param lastName The lastname attempting to log in
     * @param ipAddress The IP address of the login attempt
     * @param userAgent The browser/client information
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailedAttempt(String firstName, String lastName, String ipAddress, String userAgent) {
        logAttempt(firstName, lastName, ipAddress, userAgent, false, "Invalid credentials");
    }

    private void logAttempt(String firstName, String lastName, String ipAddress, String userAgent, boolean success, String failureReason) {
        LoginAttempt attempt = LoginAttempt.builder()
                .attemptTime(LocalDateTime.now())
                .ipAddress(ipAddress)
                .successful(success)
                .username(firstName + " " + lastName)
                .failureReason(failureReason)
                .userAgent(userAgent)
                .build();

        loginAttemptRepository.save(attempt);

        if (success) {
            log.info("Successful login - User: {}, IP: {}", attempt.getUsername(), attempt.getIpAddress());
        } else {
            log.warn("Failed login - User: {}, IP: {}, Reason: {}", attempt.getUsername(), attempt.getIpAddress(), attempt.getFailureReason());
        }
    }

}