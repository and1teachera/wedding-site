package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.PasswordChangeDto;
import com.zlatenov.wedding_backend.dto.UserProfileDto;
import com.zlatenov.wedding_backend.dto.UserProfileUpdateDto;

public interface UserProfileService {
    /**
     * Get the profile of the currently authenticated user
     * @param username The username of the authenticated user
     * @return The user profile data
     */
    UserProfileDto getUserProfile(String username);
    
    /**
     * Update the profile of the currently authenticated user
     * @param username The username of the authenticated user
     * @param updateDto The data to update
     * @return The updated user profile
     */
    UserProfileDto updateUserProfile(String username, UserProfileUpdateDto updateDto);
    
    /**
     * Change the password of the currently authenticated user
     * @param username The username of the authenticated user
     * @param passwordChangeDto The password change data
     * @return True if the password was changed successfully
     */
    boolean changePassword(String username, PasswordChangeDto passwordChangeDto);
}