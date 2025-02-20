package com.zlatenov.wedding_backend.dto;

import com.zlatenov.wedding_backend.model.ResponseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestResponse {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Attendance status is required")
    private ResponseStatus status;

    private String dietaryNotes;
    private String additionalNotes;
}
