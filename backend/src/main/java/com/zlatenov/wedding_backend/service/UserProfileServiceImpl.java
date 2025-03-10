package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.PasswordChangeDto;
import com.zlatenov.wedding_backend.dto.UserProfileDto;
import com.zlatenov.wedding_backend.dto.UserProfileUpdateDto;
import com.zlatenov.wedding_backend.exception.InvalidCredentialsException;
import com.zlatenov.wedding_backend.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserProfileDto getUserProfile(String username) {
        String[] names = username.split(" ");
        if (names.length != 2) {
            throw new IllegalArgumentException("Invalid username format");
        }
        
        User user = userService.getByFirstNameAndLastName(names[0], names[1]);
        
        return UserProfileDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isAdmin(user.isAdmin())
                .build();
    }
    
    @Override
    @Transactional
    public UserProfileDto updateUserProfile(String username, UserProfileUpdateDto updateDto) {
        String[] names = username.split(" ");
        if (names.length != 2) {
            throw new IllegalArgumentException("Invalid username format");
        }
        
        User user = userService.getByFirstNameAndLastName(names[0], names[1]);
        
        // Update user data
        user.setEmail(updateDto.getEmail());
        user.setPhone(updateDto.getPhone());
        
        // Save changes
        user = userService.saveUser(user);
        
        log.info("Updated profile for user: {}", username);
        
        return UserProfileDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isAdmin(user.isAdmin())
                .build();
    }
    
    @Override
    @Transactional
    public boolean changePassword(String username, PasswordChangeDto passwordChangeDto) {
        // Validate passwords match
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new InvalidCredentialsException("New password and confirm password do not match");
        }
        
        String[] names = username.split(" ");
        if (names.length != 2) {
            throw new IllegalArgumentException("Invalid username format");
        }
        
        User user = userService.getByFirstNameAndLastName(names[0], names[1]);
        
        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userService.saveUser(user);
        
        log.info("Password changed for user: {}", username);
        
        return true;
    }
}