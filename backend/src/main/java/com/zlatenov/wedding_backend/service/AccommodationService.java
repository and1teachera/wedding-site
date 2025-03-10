package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.SingleUserAccommodationResponsesDto;

/**
 * Service for handling accommodation requests, especially single user requests
 */
public interface AccommodationService {

    /**
     * Get all single user accommodation requests
     * @return DTO containing all single user requests with summary statistics
     */
    SingleUserAccommodationResponsesDto getAllSingleUserRequests();
    
    /**
     * Approve a single user accommodation request
     * @param requestId The request ID to approve
     * @return true if approved successfully, false otherwise
     */
    boolean approveSingleUserRequest(Long requestId);
    
    /**
     * Reject a single user accommodation request
     * @param requestId The request ID to reject
     * @return true if rejected successfully, false otherwise
     */
    boolean rejectSingleUserRequest(Long requestId);
}