package com.zlatenov.wedding_backend.controller;

import com.zlatenov.wedding_backend.annotation.RateLimited;
import com.zlatenov.wedding_backend.dto.AuthenticationResponse;
import com.zlatenov.wedding_backend.dto.EmailAuthenticationRequest;
import com.zlatenov.wedding_backend.dto.NameAuthenticationRequest;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.security.JwtTokenProvider;
import com.zlatenov.wedding_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @RateLimited(requests = 5, timeWindowSeconds = 60, key = "#ip")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateByEmail(@RequestBody @Valid EmailAuthenticationRequest request, HttpServletRequest httpRequest) {
        String ipAddress = extractIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        log.info("Authentication attempt: {} , IP: {}, User-Agent: {}",
                request.getEmail(), ipAddress, userAgent);

        User user = userService.authenticateUserWithEmail(request, ipAddress, userAgent);

        String jwt = jwtTokenProvider.generateToken(user);

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(jwt)
                .userType(user.isAdmin() ? "ADMIN" : "GUEST")
                .build());
    }

    @RateLimited(requests = 5, timeWindowSeconds = 60, key = "#ip")
    @PostMapping("/login-by-names")
    public ResponseEntity<AuthenticationResponse> authenticateByNames(@RequestBody @Valid NameAuthenticationRequest request, HttpServletRequest httpRequest) {
        String ipAddress = extractIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        log.info("Authentication attempt: {} {}, IP: {}, User-Agent: {}", 
                request.getFirstName(), request.getLastName(), ipAddress, userAgent);

        try {
            User user = userService.authenticateUserWithNames(request, ipAddress, userAgent);
            log.info("User authenticated successfully: {} {}", user.getFirstName(), user.getLastName());

            String jwt = jwtTokenProvider.generateToken(user);
            log.info("JWT generated successfully, length: {}", jwt.length());

            AuthenticationResponse response = AuthenticationResponse.builder()
                    .token(jwt)
                    .userType(user.isAdmin() ? "ADMIN" : "GUEST")
                    .user(AuthenticationResponse.UserInfo.builder()
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .familyId(user.getFamily() != null ? user.getFamily().getId() : null)
                            .build())
                    .build();

            log.info("Authentication response built: token={}, userType={}, user={}", 
                    response.getToken() != null ? "present" : "null",
                    response.getUserType(),
                    response.getUser() != null ? (response.getUser().getFirstName() + " " + response.getUser().getLastName()) : "null");

            log.info("Authentication successful for user: {} {}, Family Id: {}",
                    user.getFirstName(),
                    user.getLastName(),
                    user.getFamily() != null ? user.getFamily().getId() : "none");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            log.error("Authentication error for user {} {}: {}", request.getFirstName(), request.getLastName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Handles user logout requests.
     * In a stateless JWT implementation, the server doesn't need to do any special processing
     * as the client is responsible for discarding the token. We log the event for audit purposes.
     *
     * @param principal The authenticated user principal
     * @return Empty response with 200 OK status
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Principal principal) {

        if (principal != null) {
            log.info("User '{}' logged out", principal.getName());
        } else {
            log.warn("Logout request received with no authenticated principal");
        }
        return ResponseEntity.ok().build();
    }

    private String extractIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}