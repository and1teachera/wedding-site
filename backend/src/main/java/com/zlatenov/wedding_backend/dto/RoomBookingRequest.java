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
public class RoomBookingRequest {
    private String notes;
}