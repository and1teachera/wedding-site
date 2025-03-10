package com.zlatenov.wedding_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean isAdmin;
}