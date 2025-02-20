package com.zlatenov.wedding_backend.dto;

import com.zlatenov.wedding_backend.model.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private boolean isChild;
    private ResponseStatus rsvpStatus;
    private String dietaryNotes;
}
