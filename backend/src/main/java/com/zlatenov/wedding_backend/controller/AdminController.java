package com.zlatenov.wedding_backend.controller;

import com.zlatenov.wedding_backend.dto.FamilyCreationRequest;
import com.zlatenov.wedding_backend.dto.UserCreationRequest;
import com.zlatenov.wedding_backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Creating new user: {} {}", request.getFirstName(), request.getLastName());
        adminService.createUser(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/families")
    public ResponseEntity<Void> createFamily(@RequestBody @Valid FamilyCreationRequest request) {
        log.info("Received request to create family: {}", request.getFamilyName());
        log.debug("Family creation request details - Primary User: {} {}, Family Members: {}",
                request.getPrimaryUser().getFirstName(),
                request.getPrimaryUser().getLastName(),
                request.getFamilyMembers().size());        adminService.createFamily(request);
        return ResponseEntity.ok().build();
    }
}