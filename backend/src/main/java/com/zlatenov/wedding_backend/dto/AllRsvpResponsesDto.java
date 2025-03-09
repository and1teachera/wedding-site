package com.zlatenov.wedding_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllRsvpResponsesDto {
    private List<RsvpEntryDto> responses;
    private int totalGuests;
    private int confirmedGuests;
    private int pendingGuests;
    private int declinedGuests;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsvpEntryDto {
        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
        private String status;
        private boolean isChild;
        private String dietaryNotes;
        private String additionalNotes;
        private String familyName;
    }
}