package com.zlatenov.wedding_backend.security;

import com.zlatenov.wedding_backend.BaseIntegrationTest;
import com.zlatenov.wedding_backend.exception.MalformedTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest extends BaseIntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private final UserDetails userDetails = new User("test@example.com", "password", Collections.emptyList());

    @Test
    @DisplayName("Should generate valid JWT token with correct username")
    void shouldGenerateValidToken() {
        String token = jwtTokenProvider.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userDetails.getUsername(), jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Should reject invalid JWT token")
    void shouldRejectInvalidToken() {
        assertThrows(MalformedTokenException.class, () -> jwtTokenProvider.validateToken("invalid.token.here"));
    }
}