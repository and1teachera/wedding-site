package com.zlatenov.wedding_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Angel Zlatenov
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginByEmailRequest {
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
