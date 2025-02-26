package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.model.Family;

/**
 * @author Angel Zlatenov
 */

public interface FamilyService {

    /**
     * Get a family by Id
     * @param id The family Id
     * @return The family entity
     */
    Family getFamilyById(Long id);

    /**
     * Check if any family member has RSVP'd with YES status
     * @param family The family entity
     * @return true if at least one member has RSVP'd YES
     */
    boolean hasFamilyMembersAttending(Family family);
}
