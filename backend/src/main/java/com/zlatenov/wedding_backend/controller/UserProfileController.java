package com.zlatenov.wedding_backend.controller;

import com.zlatenov.wedding_backend.dto.PasswordChangeDto;
import com.zlatenov.wedding_backend.dto.UserProfileDto;
import com.zlatenov.wedding_backend.dto.UserProfileUpdateDto;
import com.zlatenov.wedding_backend.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(Authentication authentication) {
        log.info("Getting profile for user: {}", authentication.getName());
        UserProfileDto profile = userProfileService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @RequestBody @Valid UserProfileUpdateDto updateDto,
            Authentication authentication) {
        log.info("Updating profile for user: {}", authentication.getName());
        UserProfileDto updatedProfile = userProfileService.updateUserProfile(authentication.getName(), updateDto);
        return ResponseEntity.ok(updatedProfile);
    }
    
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid PasswordChangeDto passwordChangeDto,
            Authentication authentication) {
        log.info("Changing password for user: {}", authentication.getName());
        boolean success = userProfileService.changePassword(authentication.getName(), passwordChangeDto);
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}