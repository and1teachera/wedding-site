package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.FamilyOverviewDto;

/**
 * Interface for family and guest overview service
 */
public interface FamilyOverviewService {
    
    /**
     * Get a complete overview of all families and single guests
     * @return DTO containing all family and guest information
     */
    FamilyOverviewDto getFamiliesOverview();
}