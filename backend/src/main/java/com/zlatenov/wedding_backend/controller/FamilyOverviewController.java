package com.zlatenov.wedding_backend.controller;

import com.zlatenov.wedding_backend.dto.FamilyOverviewDto;
import com.zlatenov.wedding_backend.service.FamilyOverviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for admin family overview functionalities
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class FamilyOverviewController {

    private final FamilyOverviewService familyOverviewService;

    /**
     * Get overview of all families, their members, and single users
     * @return FamilyOverviewDto with all family and user data
     */
    @GetMapping("/families-overview")
    public ResponseEntity<FamilyOverviewDto> getFamiliesOverview() {
        log.info("Request received to get families overview");
        
        FamilyOverviewDto overview = familyOverviewService.getFamiliesOverview();
        
        log.info("Returning families overview: {} families, {} single users", 
                overview.getTotalFamilies(), overview.getTotalSingleUsers());
        
        return ResponseEntity.ok(overview);
    }
}