package com.zlatenov.wedding_backend.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateDto {
    @Email(message = "Please provide a valid email address")
    private String email;
    private String phone;
}