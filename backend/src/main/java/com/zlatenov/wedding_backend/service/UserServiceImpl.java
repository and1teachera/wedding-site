package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.exception.InvalidCredentialsException;
import com.zlatenov.wedding_backend.exception.UserNotFoundException;
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

import java.util.List;

/**
 * Service responsible for user authentication operations.
 * Handles both email-based and name-based authentication while ensuring
 * secure password validation.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements com.zlatenov.wedding_backend.service.UserService {

    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String USER_NOT_FOUND = "User not found %s";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User authenticateUserWithNames(@NotNull(message = "First name is required") @NotBlank(message = "First name cannot be blank")
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

    @Override
    public User authenticateUserWithEmail(
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

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, id)));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, email)));
    }

    @Override
    public User getByFirstNameAndLastName(String firstName, String lastName) {
        return userRepository.findByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, firstName + " " + lastName)));
    }

    @Override
    public List<User> getFamilyMembers(Long familyId) {
        return userRepository.findByFamilyId(familyId);
    }

    /**
     * Updates a user's password. The new password will be hashed using BCrypt
     * before being stored in the database.
     *
     * @param userId Id of the user whose password needs to be updated
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
}