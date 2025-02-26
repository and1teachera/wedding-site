package com.zlatenov.wedding_backend.service;

import org.springframework.scheduling.annotation.Async;

/**
 * @author Angel Zlatenov
 */

public interface LoginAuditService {

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
    void logSuccessfulAttempt(String firstName, String lastName, String ipAddress, String userAgent);

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
    void logFailedAttempt(String firstName, String lastName, String ipAddress, String userAgent);
}
