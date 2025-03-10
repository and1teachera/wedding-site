package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.EmailAuthenticationRequest;
import com.zlatenov.wedding_backend.dto.NameAuthenticationRequest;
import com.zlatenov.wedding_backend.exception.InvalidCredentialsException;
import com.zlatenov.wedding_backend.exception.UserNotFoundException;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.repository.UserRepository;
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
    public User authenticateUserWithNames(NameAuthenticationRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByFirstNameAndLastName(request.getFirstName(), request.getLastName())
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS));

        boolean isValid = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!isValid) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }

        return user;
    }

    @Override
    public User authenticateUserWithEmail(EmailAuthenticationRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS));

        boolean isValid = passwordEncoder.matches(request.getPassword(), user.getPassword());

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
    
    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
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