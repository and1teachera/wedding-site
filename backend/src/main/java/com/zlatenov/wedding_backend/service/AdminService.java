package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.FamilyCreationRequest;
import com.zlatenov.wedding_backend.dto.UserCreationRequest;

/**
 * @author Angel Zlatenov
 */

public interface AdminService {

    /**
     * Create a user from a user creation request
     *
     * @param request The user creation request containing user information
     */
    void createUser(UserCreationRequest request);

    /**
     * Create a family from a family creation request
     *
     * @param request The family creation request containing family members
     */
    void createFamily(FamilyCreationRequest request);
}
