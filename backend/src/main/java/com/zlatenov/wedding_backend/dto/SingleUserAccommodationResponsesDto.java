package com.zlatenov.wedding_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SingleUserAccommodationResponsesDto {
    private List<SingleUserRequestDto> requests;
    private int totalRequests;
    private int pendingRequests;
    private int processedRequests;
    private int cancelledRequests;

    @Data
    @Builder
    public static class SingleUserRequestDto {
        private Long requestId;
        private Long userId;
        private String userName;
        private LocalDateTime requestDate;
        private String status;
        private String notes;
        private boolean processed;
        private LocalDateTime processedDate;
    }
}