package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;


/**
 * @author Angel Zlatenov
 */


@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Extract the family ID from the authentication object
     * @param authentication The authentication object
     * @return Family ID or null if not associated with a family
     */
    public Long getFamilyIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        String[] names = username.split(" ");
        if (names.length != 2) {
            throw new IllegalArgumentException("Invalid username format");
        }

        return jwtTokenProvider.getFamilyIdFromUsername(names[0], names[1]);
    }
}