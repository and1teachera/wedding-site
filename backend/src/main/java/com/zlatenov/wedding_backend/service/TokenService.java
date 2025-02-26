package com.zlatenov.wedding_backend.service;

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
    private final UserService userService;

    /**
     * Extract the family Id from the authentication object
     * @param authentication The authentication object
     * @return Family Id or null if not associated with a family
     */
    public Long getFamilyIdFromAuthentication(Authentication authentication) {
        String[] names = validateUserName(authentication);

        return jwtTokenProvider.getFamilyIdFromUsername(names[0], names[1]);
    }

    /**
     * Extract the user Id from the authentication object
     * @param authentication The authentication object
     * @return User Id
     */
    public Long getUserIdFromAuthentication(Authentication authentication) {
        String[] names = validateUserName(authentication);

        return userService.getByFirstNameAndLastName(names[0], names[1]).getId();
    }

    private String[] validateUserName(Authentication authentication) {
        String username = authentication.getName();
        String[] names = username.split(" ");
        if (names.length != 2) {
            throw new IllegalArgumentException("Invalid username format");
        }
        return names;
    }
}