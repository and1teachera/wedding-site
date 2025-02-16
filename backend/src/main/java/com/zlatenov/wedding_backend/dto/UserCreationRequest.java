package com.zlatenov.wedding_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreationRequest {
    @NotNull(message = "First name is required")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotNull(message = "Last name is required")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    private String email;

    private String phone;

    @Builder.Default
    private String defaultPassword = "mywedding";

    @Builder.Default
    private boolean isChild = false;

    @Builder.Default
    private boolean isAdmin = false;

    private Long familyId;
}