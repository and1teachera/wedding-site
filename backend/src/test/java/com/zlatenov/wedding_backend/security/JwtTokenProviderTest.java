package com.zlatenov.wedding_backend.security;

import com.zlatenov.wedding_backend.BaseIntegrationTest;
import com.zlatenov.wedding_backend.exception.InvalidTokenSignatureException;
import com.zlatenov.wedding_backend.exception.MalformedTokenException;
import com.zlatenov.wedding_backend.exception.TokenExpiredException;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.repository.UserRepository;
import com.zlatenov.wedding_backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest extends BaseIntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private final User testUser = User.builder()
            .firstName("Test")
            .lastName("User")
            .password("password")
            .build();

    @Test
    @DisplayName("Should generate valid JWT token with correct username")
    void shouldGenerateValidToken() {
        String token = jwtTokenProvider.generateToken(testUser);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(testUser.getFirstName() + " " + testUser.getLastName(), jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Should reject invalid JWT token")
    void shouldRejectInvalidToken() {
        assertThrows(MalformedTokenException.class, () -> jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    @DisplayName("Should throw InvalidTokenSignatureException for invalid signature")
    void shouldThrowExceptionForInvalidSignature() {
        String token = jwtTokenProvider.generateToken(testUser);
        String tamperedToken = token.substring(0, token.lastIndexOf('.')) + ".invalid_signature";

        assertThrows(InvalidTokenSignatureException.class, () ->
                jwtTokenProvider.validateToken(tamperedToken)
        );
    }

    @Test
    @DisplayName("Should validate token structure")
    void shouldValidateTokenStructure() {
        String token = jwtTokenProvider.generateToken(testUser);
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
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 1L);
        String token = jwtTokenProvider.generateToken(testUser);

        Thread.sleep(10);

        assertThrows(TokenExpiredException.class, () -> jwtTokenProvider.validateToken(token));
    }
}