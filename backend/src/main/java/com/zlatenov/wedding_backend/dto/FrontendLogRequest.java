package com.zlatenov.wedding_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for receiving frontend log batches
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontendLogRequest {
    
    @NotEmpty(message = "Log entries cannot be empty")
    private List<@Valid FrontendLogEntry> logs;
    
    @NotNull(message = "Client info is required")
    @Valid
    private ClientInfo clientInfo;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientInfo {
        @NotNull(message = "User agent is required")
        private String userAgent;
        
        @NotNull(message = "URL is required")
        private String url;
        
        @NotNull(message = "Timestamp is required")
        private String timestamp;
    }
}