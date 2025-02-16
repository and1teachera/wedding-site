package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.exception.InvalidCredentialsException;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.repository.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for user authentication operations.
 * Handles both email-based and name-based authentication while ensuring
 * secure password validation.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user using their email and password.
     * Uses constant-time comparison through BCrypt to prevent timing attacks.
     *
     * @param email User's email address
     * @param password Raw password to validate
     * @return The authenticated user if credentials are valid
     * @throws InvalidCredentialsException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public User authenticateByEmail(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
    }

    /**
     * Authenticates a user using their first name, last name, and password.
     * Uses constant-time comparison through BCrypt to prevent timing attacks.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @param password Raw password to validate
     * @return The authenticated user if credentials are valid
     * @throws InvalidCredentialsException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public User authenticateByNames(String firstName, String lastName, String password) {
        return userRepository.findByFirstNameAndLastName(firstName, lastName)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid name or password"));
    }

    /**
     * Updates a user's password. The new password will be hashed using BCrypt
     * before being stored in the database.
     *
     * @param userId ID of the user whose password needs to be updated
     * @param newPassword New password in plain text
     */
    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        userRepository.findById(userId)
                .ifPresent(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                });
    }

    /**
     * Finds a user by their first and last name.
     * Used during the authentication process.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @return The found user
     * @throws InvalidCredentialsException if user is not found
     */
    @Transactional(readOnly = true)
    public User findByFirstNameAndLastName(
            @NotNull(message = "First name is required")
            @NotBlank(message = "First name cannot be blank")
            @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
            String firstName,
            @NotNull(message = "Last name is required")
            @NotBlank(message = "Last name cannot be blank")
            @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
            String lastName) {

        return userRepository.findByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid name or password"));
    }

    public User authenticateUser(@NotNull(message = "First name is required") @NotBlank(message = "First name cannot be blank")
                                 @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") String firstName,
                                 @NotNull(message = "Last name is required")
                                 @NotBlank(message = "Last name cannot be blank")
                                 @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") String lastName,
                                 @NotNull(message = "Password is required")
                                 @NotBlank(message = "Password cannot be blank") @Size(min = 8, message = "Password must be at least 8 characters long")
                                 String password, String ipAddress, String userAgent) {
        User user = userRepository.findByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS));

        boolean isValid = passwordEncoder.matches(password, user.getPassword());

        if (!isValid) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }

        return user;
    }

    public User authenticateUser(
            @NotNull(message = "Email is required") @NotBlank(message = "Email cannot be blank") @Email(message = "Please provide a valid email address")
            String email, @NotNull(message = "Password is required")
            @NotBlank(message = "Password cannot be blank") String password, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS));

        boolean isValid = passwordEncoder.matches(password, user.getPassword());

        if (!isValid) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }

        return user;
    }
}