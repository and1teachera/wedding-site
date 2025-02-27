package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.LoginAttempt;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAttemptRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("Should save and retrieve login attempt")
    @Test
    void shouldSaveAndRetrieveLoginAttempt() {
        // Create and save login attempt
        LoginAttempt attempt = LoginAttempt.builder()
                .attemptTime(LocalDateTime.now())
                .ipAddress("192.168.1.1")
                .successful(true)
                .username("test.user")
                .userAgent("Mozilla/5.0")
                .build();

        loginAttemptRepository.save(attempt);

        // Retrieve and verify
        LoginAttempt savedAttempt = loginAttemptRepository.findById(attempt.getId()).orElseThrow();

        assertThat(savedAttempt).isNotNull();
        assertThat(savedAttempt.getIpAddress()).isEqualTo("192.168.1.1");
        assertThat(savedAttempt.isSuccessful()).isTrue();
        assertThat(savedAttempt.getUsername()).isEqualTo("test.user");
        assertThat(savedAttempt.getUserAgent()).isEqualTo("Mozilla/5.0");
    }

    @DisplayName("Should count recent failed attempts")
    @Test
    void shouldCountRecentFailedAttempts() {
        // Setup base time
        LocalDateTime now = LocalDateTime.now();
        String testIpAddress = "192.168.1.100";

        // Create failed attempts within time window
        LoginAttempt recentFailed1 = createLoginAttempt(testIpAddress, now.minusMinutes(5), false);
        LoginAttempt recentFailed2 = createLoginAttempt(testIpAddress, now.minusMinutes(10), false);
        LoginAttempt recentFailed3 = createLoginAttempt(testIpAddress, now.minusMinutes(15), false);

        // Create attempt outside time window (30 minutes ago)
        LoginAttempt oldFailed = createLoginAttempt(testIpAddress, now.minusMinutes(35), false);

        // Create successful attempt (should not be counted)
        LoginAttempt successful = createLoginAttempt(testIpAddress, now.minusMinutes(7), true);

        // Create failed attempt with different IP (should not be counted)
        LoginAttempt differentIp = createLoginAttempt("192.168.1.200", now.minusMinutes(8), false);

        // Flush to ensure all entities are persisted
        entityManager.flush();

        // Count recent failed attempts (last 30 minutes)
        long count = loginAttemptRepository.countRecentFailedAttempts(testIpAddress, now.minusMinutes(30));

        // Should count 3 failed attempts within 30 minutes
        assertThat(count).isEqualTo(3);
    }

    @DisplayName("Should return zero when no failed attempts exist")
    @Test
    void shouldReturnZeroWhenNoFailedAttemptsExist() {
        String ipAddress = "10.0.0.1";
        LocalDateTime since = LocalDateTime.now().minusMinutes(60);

        // No attempts created for this IP

        long count = loginAttemptRepository.countRecentFailedAttempts(ipAddress, since);

        assertThat(count).isZero();
    }

    @DisplayName("Should only count failed attempts")
    @Test
    void shouldOnlyCountFailedAttempts() {
        LocalDateTime now = LocalDateTime.now();
        String ipAddress = "10.0.0.2";

        // Create only successful attempts
        createLoginAttempt(ipAddress, now.minusMinutes(5), true);
        createLoginAttempt(ipAddress, now.minusMinutes(10), true);
        createLoginAttempt(ipAddress, now.minusMinutes(15), true);

        entityManager.flush();

        long count = loginAttemptRepository.countRecentFailedAttempts(ipAddress, now.minusMinutes(30));

        assertThat(count).isZero();
    }

    private LoginAttempt createLoginAttempt(String ipAddress, LocalDateTime attemptTime, boolean successful) {
        LoginAttempt attempt = LoginAttempt.builder()
                .attemptTime(attemptTime)
                .ipAddress(ipAddress)
                .successful(successful)
                .username("test.user")
                .userAgent("Test User Agent")
                .build();

        return loginAttemptRepository.save(attempt);
    }
}