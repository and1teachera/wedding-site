package com.zlatenov.wedding_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Angel Zlatenov
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private long timestamp;

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, System.currentTimeMillis());
    }
}
