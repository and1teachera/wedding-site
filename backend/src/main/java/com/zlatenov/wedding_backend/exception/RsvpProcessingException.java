package com.zlatenov.wedding_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RsvpProcessingException extends RuntimeException {
    public RsvpProcessingException(String message) {
        super(message);
    }
}
