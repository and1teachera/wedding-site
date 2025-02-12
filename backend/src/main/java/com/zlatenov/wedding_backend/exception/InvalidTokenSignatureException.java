package com.zlatenov.wedding_backend.exception;

public class InvalidTokenSignatureException extends RuntimeException {
    public InvalidTokenSignatureException(String message) {
        super(message);
    }
}