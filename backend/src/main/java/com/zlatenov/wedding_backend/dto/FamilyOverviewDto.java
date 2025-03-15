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
public class FamilyOverviewDto {
    private List<FamilyDto> families;
    private List<SingleUserDto> singleUsers;
    private int totalFamilies;
    private int totalSingleUsers;
    private int totalGuests;
    private int confirmedGuests;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FamilyDto {
        private Long id;
        private String name;
        private List<FamilyMemberDto> members;
        private boolean hasRoomBooked;
        private Integer roomNumber;
        private int totalMembers;
        private int confirmedMembers;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FamilyMemberDto {
        private Long id;
        private String firstName;
        private String lastName;
        private boolean isChild;
        private String rsvpStatus;
        private String dietaryNotes;
        private String additionalNotes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleUserDto {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String rsvpStatus;
        private boolean hasAccommodationRequest;
        private String accommodationStatus;
    }
}