package com.zlatenov.wedding_backend.exception;

/**
 * @author Angel Zlatenov
 */

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
