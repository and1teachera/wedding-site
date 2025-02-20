package com.zlatenov.wedding_backend.controller;

import com.zlatenov.wedding_backend.annotation.RateLimited;
import com.zlatenov.wedding_backend.dto.AuthenticationResponse;
import com.zlatenov.wedding_backend.dto.LoginByEmailRequest;
import com.zlatenov.wedding_backend.dto.LoginByNamesRequest;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.security.JwtTokenProvider;
import com.zlatenov.wedding_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @RateLimited(requests = 5, timeWindowSeconds = 60, key = "#ip")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateByEmail(@RequestBody @Valid LoginByEmailRequest request, HttpServletRequest httpRequest) {
        String ipAddress = extractIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        log.info("Authentication attempt: {} , IP: {}, User-Agent: {}",
                request.getEmail(), ipAddress, userAgent);

        User user = userService.authenticateUser(request.getEmail(), request.getPassword(), ipAddress, userAgent);

        String jwt = jwtTokenProvider.generateToken(user);

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(jwt)
                .userType(user.isAdmin() ? "ADMIN" : "GUEST")
                .build());
    }

    @RateLimited(requests = 5, timeWindowSeconds = 60, key = "#ip")
    @PostMapping("/login-by-names")
    public ResponseEntity<AuthenticationResponse> authenticateByNames(@RequestBody @Valid LoginByNamesRequest request, HttpServletRequest httpRequest) {

        String ipAddress = extractIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        log.info("Authentication attempt: {} {}, IP: {}, User-Agent: {}",
                request.getFirstName(), request.getLastName(), ipAddress, userAgent);

        User user = userService.authenticateUser(request.getFirstName(), request.getLastName(), request.getPassword(), ipAddress, userAgent);

        String jwt = jwtTokenProvider.generateToken(user);

        AuthenticationResponse response = AuthenticationResponse.builder()
                .token(jwt)
                .userType(user.isAdmin() ? "ADMIN" : "GUEST")
                .user(AuthenticationResponse.UserInfo.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .familyId(user.getFamily() != null ? user.getFamily().getId() : null)
                        .build())
                .build();


        log.info("Authentication successful for user: {} {}, Family ID: {}",
                user.getFirstName(),
                user.getLastName(),
                user.getFamily() != null ? user.getFamily().getId() : "none");

        return ResponseEntity.ok(response);
    }

    private String extractIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}