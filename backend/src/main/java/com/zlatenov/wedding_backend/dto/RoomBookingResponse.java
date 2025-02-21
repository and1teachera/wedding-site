package com.zlatenov.wedding_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Angel Zlatenov
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomBookingResponse {
    private boolean success;
    private String message;
    private Integer roomNumber;
    private Long familyId;
    private String status;
    private String notes;
}