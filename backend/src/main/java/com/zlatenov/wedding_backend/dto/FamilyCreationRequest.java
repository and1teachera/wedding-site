package com.zlatenov.wedding_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyCreationRequest {
    @NotNull(message = "Primary user is required")
    @Valid
    private UserCreationRequest primaryUser;

    @NotNull(message = "Family name is required when creating a family")
    @NotBlank(message = "Family name cannot be blank")
    @Size(min = 2, max = 100, message = "Family name must be between 2 and 100 characters")
    private String familyName;

    @NotNull(message = "Family members list cannot be null")
    @NotEmpty(message = "Family members list cannot be empty")
    @Valid
    private List<UserCreationRequest> familyMembers;
}