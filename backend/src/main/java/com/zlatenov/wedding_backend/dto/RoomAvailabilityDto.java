package com.zlatenov.wedding_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityDto {
    private List<RoomInfoDto> rooms;
    private int totalRooms;
    private int availableRooms;
    private int bookedRooms;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomInfoDto {
        private Long roomId;
        private Integer roomNumber;
        private boolean available;
        private BookingInfoDto booking;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingInfoDto {
        private Long bookingId;
        private String status;
        private LocalDateTime bookingTime;
        private String bookedBy; // Family name or guest name
        private Long familyId;
        private String notes;
    }
}