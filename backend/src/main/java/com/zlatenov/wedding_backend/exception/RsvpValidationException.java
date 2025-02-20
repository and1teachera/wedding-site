package com.zlatenov.wedding_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RsvpValidationException extends RuntimeException {
    public RsvpValidationException(String message) {
        super(message);
    }
}
