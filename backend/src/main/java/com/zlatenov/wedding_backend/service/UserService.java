package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.EmailAuthenticationRequest;
import com.zlatenov.wedding_backend.dto.NameAuthenticationRequest;
import com.zlatenov.wedding_backend.model.User;

import java.util.List;

/**
 * @author Angel Zlatenov
 */

public interface UserService {
    User authenticateUserWithEmail(EmailAuthenticationRequest request, String ipAddress, String userAgent);

    User authenticateUserWithNames(NameAuthenticationRequest request, String ipAddress, String userAgent);

    User getUserById(Long id);

    User getByEmail(String email);

    User getByFirstNameAndLastName(String firstName, String lastName);

    List<User> getFamilyMembers(Long familyId);
    
    /**
     * Save user changes
     * @param user The user to save
     * @return The saved user
     */
    User saveUser(User user);
}
