package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.exception.ResourceNotFoundException;
import com.zlatenov.wedding_backend.repository.UserRepository;
import com.zlatenov.wedding_backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


/**
 * @author Angel Zlatenov
 */


@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * Extract the family ID from the authentication object
     * @param authentication The authentication object
     * @return Family ID or null if not associated with a family
     */
    public Long getFamilyIdFromAuthentication(Authentication authentication) {
        String[] names = validateUserName(authentication);

        return jwtTokenProvider.getFamilyIdFromUsername(names[0], names[1]);
    }

    private String[] validateUserName(Authentication authentication) {
        String username = authentication.getName();
        String[] names = username.split(" ");
        if (names.length != 2) {
            throw new IllegalArgumentException("Invalid username format");
        }
        return names;
    }

    /**
     * Extract the user ID from the authentication object
     * @param authentication The authentication object
     * @return User ID
     */
    public Long getUserIdFromAuthentication(Authentication authentication) {
        String[] names = validateUserName(authentication);

        return userRepository.findByFirstNameAndLastName(names[0], names[1])
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getId();
    }
}