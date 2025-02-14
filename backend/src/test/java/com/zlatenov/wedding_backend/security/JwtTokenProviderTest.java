package com.zlatenov.wedding_backend.security;

import com.zlatenov.wedding_backend.BaseIntegrationTest;
import com.zlatenov.wedding_backend.exception.InvalidTokenSignatureException;
import com.zlatenov.wedding_backend.exception.MalformedTokenException;
import com.zlatenov.wedding_backend.exception.TokenExpiredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Test
    @DisplayName("Should throw InvalidTokenSignatureException for expired token")
    void shouldThrowExceptionForInvalidSignatureToken() {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiZXhwIjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        assertThrows(InvalidTokenSignatureException.class, () ->
            jwtTokenProvider.validateToken(expiredToken)
        );
    }

    @Test
    @DisplayName("Should throw MalformedTokenException for malformed token")
    void shouldThrowExceptionForMalformedToken() {
        String malformedToken = "invalid.jwt.token";
        
        assertThrows(MalformedTokenException.class, () -> 
            jwtTokenProvider.validateToken(malformedToken)
        );
    }

    @Test
    @DisplayName("Should throw InvalidTokenSignatureException for invalid signature")
    void shouldThrowExceptionForInvalidSignature() {
        String token = jwtTokenProvider.generateToken(userDetails);
        String tamperedToken = token.substring(0, token.lastIndexOf('.')) + ".invalid_signature";
        
        assertThrows(InvalidTokenSignatureException.class, () -> 
            jwtTokenProvider.validateToken(tamperedToken)
        );
    }

    @Test
    @DisplayName("Should validate token structure")
    void shouldValidateTokenStructure() {
        String token = jwtTokenProvider.generateToken(userDetails);
        String[] parts = token.split("\\.");
        
        assertEquals(3, parts.length, "JWT should have three parts");
        assertNotNull(parts[0], "Header should not be null");
        assertNotNull(parts[1], "Payload should not be null");
        assertNotNull(parts[2], "Signature should not be null");
    }

    @Test
    @DisplayName("Should throw MalformedTokenException for null token")
    void shouldThrowExceptionForNullToken() {
        assertThrows(MalformedTokenException.class, () -> jwtTokenProvider.validateToken(null));
    }

    @Test
    @DisplayName("Should throw TokenExpiredException for expired token")
    void shouldThrowExceptionForExpiredToken() throws InterruptedException {
        // Set a very short expiration time for testing
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 1L);
        String token = jwtTokenProvider.generateToken(userDetails);

        // Wait for token to expire
        Thread.sleep(10);

        assertThrows(TokenExpiredException.class, () -> jwtTokenProvider.validateToken(token));
    }
}