package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.exception.TooManyRequestsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RateLimiterServiceTest {

    @Test
    @DisplayName("should allow requests under max rate limit")
    void shouldAllowRequestsUnderMaxRateLimit() {
        // Arrange
        RateLimiterService rateLimiterService = new RateLimiterService();
        String key = "test_key";
        int maxRequests = 5;

        // Act & Assert
        // This should not throw any exception
        for (int i = 0; i < maxRequests; i++) {
            rateLimiterService.checkRateLimit(key, maxRequests);
        }
    }

    @Test
    @DisplayName("should throw exception when rate limit is exceeded")
    void shouldThrowExceptionWhenRateLimitIsExceeded() {
        // Arrange
        RateLimiterService rateLimiterService = new RateLimiterService();
        String key = "test_key";
        int maxRequests = 3;

        // Act & Assert
        // First make enough requests to reach the limit
        for (int i = 0; i < maxRequests; i++) {
            rateLimiterService.checkRateLimit(key, maxRequests);
        }

        // The next request should throw an exception
        assertThrows(TooManyRequestsException.class, () ->
                rateLimiterService.checkRateLimit(key, maxRequests)
        );
    }

    @Test
    @DisplayName("should track different keys separately")
    void shouldTrackDifferentKeysSeparately() {
        // Arrange
        RateLimiterService rateLimiterService = new RateLimiterService();
        String key1 = "test_key_1";
        String key2 = "test_key_2";
        int maxRequests = 3;

        // Act
        // Make enough requests with key1 to reach the limit
        for (int i = 0; i < maxRequests; i++) {
            rateLimiterService.checkRateLimit(key1, maxRequests);
        }

        // Assert
        // key1 should be rate limited
        assertThrows(TooManyRequestsException.class, () ->
                rateLimiterService.checkRateLimit(key1, maxRequests)
        );

        // key2 should still allow requests
        assertDoesNotThrow(() ->
                rateLimiterService.checkRateLimit(key2, maxRequests)
        );
    }

    @Test
    @DisplayName("should check rate limit with different max request values")
    void shouldCheckRateLimitWithDifferentMaxRequestValues() {
        // Arrange
        RateLimiterService rateLimiterService = new RateLimiterService();
        String key = "test_key";

        // Act & Assert
        // Set max requests to 1
        rateLimiterService.checkRateLimit(key, 1);
        assertThrows(TooManyRequestsException.class, () ->
                rateLimiterService.checkRateLimit(key, 1)
        );

        // Different key with max requests 3
        String key2 = "test_key_2";
        rateLimiterService.checkRateLimit(key2, 3);
        rateLimiterService.checkRateLimit(key2, 3);
        rateLimiterService.checkRateLimit(key2, 3);
        assertThrows(TooManyRequestsException.class, () ->
                rateLimiterService.checkRateLimit(key2, 3)
        );
    }
}