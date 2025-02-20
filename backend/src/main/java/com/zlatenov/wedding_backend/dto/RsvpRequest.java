package com.zlatenov.wedding_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsvpRequest {
    @NotNull(message = "Primary guest response is required")
    @Valid
    private GuestResponse primaryGuest;

    @Valid
    private List<GuestResponse> familyMembers;

    @Valid
    private AccommodationRequest accommodation;
}

