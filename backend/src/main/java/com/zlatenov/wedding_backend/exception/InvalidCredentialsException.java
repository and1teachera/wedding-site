package com.zlatenov.wedding_backend.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Angel Zlatenov
 */

public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
