package com.zlatenov.wedding_backend.controller;

import com.zlatenov.wedding_backend.dto.AllRsvpResponsesDto;
import com.zlatenov.wedding_backend.dto.FamilyMembersResponse;
import com.zlatenov.wedding_backend.dto.GuestResponse;
import com.zlatenov.wedding_backend.dto.RsvpRequest;
import com.zlatenov.wedding_backend.dto.RsvpResponse;
import com.zlatenov.wedding_backend.service.RsvpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rsvp")
@RequiredArgsConstructor
public class RsvpController {

    private final RsvpService rsvpService;

    @PostMapping
    public ResponseEntity<RsvpResponse> submitRsvp(
            @RequestBody @Valid RsvpRequest request,
            Authentication authentication) {
        log.info("Received RSVP submission for family from user: {}", authentication.getName());
        log.debug("RSVP request details: {}", request);

        RsvpResponse response = rsvpService.processRsvp(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/family-members")
    public ResponseEntity<FamilyMembersResponse> getFamilyMembers(Authentication authentication) {
        log.info("Retrieving family members for user: {}", authentication.getName());
        FamilyMembersResponse response = rsvpService.getFamilyMembers(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<RsvpResponse> getRsvpStatus(Authentication authentication) {
        log.info("Retrieving RSVP status for user: {}", authentication.getName());

        RsvpResponse response = rsvpService.getRsvpStatus(authentication.getName());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/primary")
    public ResponseEntity<RsvpResponse> savePrimaryGuestResponse(
            @RequestBody @Valid GuestResponse request,
            Authentication authentication) {

        log.info("Saving primary guest response for user: {}", authentication.getName());
        log.debug("Primary guest response details: {}", request);

        RsvpResponse response = rsvpService.savePrimaryGuestResponse(request, authentication.getName());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin/all-responses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AllRsvpResponsesDto> getAllRsvpResponses() {
        log.info("Admin request to get all RSVP responses");
        AllRsvpResponsesDto responses = rsvpService.getAllRsvpResponses();
        return ResponseEntity.ok(responses);
    }
}