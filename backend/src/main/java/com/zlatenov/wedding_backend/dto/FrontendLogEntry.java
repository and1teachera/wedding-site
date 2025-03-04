package com.zlatenov.wedding_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for individual frontend log entries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontendLogEntry {
    @NotNull(message = "Timestamp is required")
    private String timestamp;
    
    @NotNull(message = "Log level is required")
    private Integer level;
    
    @NotNull(message = "Log message is required")
    private String message;
    
    private String context;
    private String userId;
    private String requestId;
    private String traceId;
    private Map<String, Object> additional;
    private String stack;
}