package com.zlatenov.wedding_backend.exception;

/**
 * Exception thrown when a client exceeds the allowed rate limit for requests
 */
public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(String message) {
        super(message);
    }
}