package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * @author Angel Zlatenov
 */

public interface UserService {

    User authenticateUserWithEmail(
            @NotNull(message = "Email is required") @NotBlank(message = "Email cannot be blank") @Email(message = "Please provide a valid email address")
            String email, @NotNull(message = "Password is required")
            @NotBlank(message = "Password cannot be blank") String password, String ipAddress, String userAgent);

    User getUserById(Long id);

    User getByEmail(String email);

    List<User> getFamilyMembers(Long familyId);

    User getByFirstNameAndLastName(String firstName, String lastName);

    User authenticateUserWithNames(@NotNull(message = "First name is required") @NotBlank(message = "First name cannot be blank")
                          @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") String firstName, @NotNull(message = "Last name is required")
                          @NotBlank(message = "Last name cannot be blank")
                          @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") String lastName, @NotNull(message = "Password is required")
                          @NotBlank(message = "Password cannot be blank") @Size(min = 8, message = "Password must be at least 8 characters long") String password, String ipAddress, String userAgent);
}
