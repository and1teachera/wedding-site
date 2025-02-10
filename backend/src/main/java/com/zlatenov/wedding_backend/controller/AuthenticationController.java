package com.zlatenov.wedding_backend.controller;

import com.zlatenov.wedding_backend.dto.LoginByNamesRequest;
import com.zlatenov.wedding_backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.zlatenov.wedding_backend.dto.LoginByEmailRequest;
import jakarta.validation.Valid;
import com.zlatenov.wedding_backend.dto.AuthenticationResponse;

/**
 * @author Angel Zlatenov
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateByEmail(
            @RequestBody @Valid LoginByEmailRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenProvider.generateToken(userDetails);
        String userType = userDetails.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("GUEST");

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(jwt)
                .userType(userType)
                .build());
    }

    @PostMapping("/login-by-names")
    public ResponseEntity<AuthenticationResponse> authenticateByNames(
            @RequestBody @Valid LoginByNamesRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        String.format("%s %s", request.getFirstName(), request.getLastName()),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenProvider.generateToken(userDetails);
        String userType = userDetails.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("GUEST");

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(jwt)
                .userType(userType)
                .build());
    }
}