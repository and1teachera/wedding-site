package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.FamilyMembersResponse;
import com.zlatenov.wedding_backend.dto.GuestResponse;
import com.zlatenov.wedding_backend.dto.RsvpRequest;
import com.zlatenov.wedding_backend.dto.RsvpResponse;
import jakarta.validation.Valid;

/**
 * @author Angel Zlatenov
 */

public interface RsvpService {

    /**
     * Process an RSVP submission
     *
     * @param request The RSVP request containing guest responses
     * @param username The authenticated username (first and last name)
     * @return Response with success status and information
     */
    RsvpResponse processRsvp(RsvpRequest request, String username);

    /**
     * Get family members for the RSVP form
     *
     * @param name The authenticated username
     * @return Response with primary user and family members information
     */
    FamilyMembersResponse getFamilyMembers(String name);

    /**
     * Get the current RSVP status for a user and their family
     *
     * @param name The authenticated username
     * @return Response with the current RSVP status
     */
    RsvpResponse getRsvpStatus(String name);

    /**
     * Save the primary guest's response only
     * @param guestResponse The primary guest's response
     * @param username The authenticated user's username
     * @return Response with success status
     */
    RsvpResponse savePrimaryGuestResponse(@Valid GuestResponse guestResponse, String username);
}
