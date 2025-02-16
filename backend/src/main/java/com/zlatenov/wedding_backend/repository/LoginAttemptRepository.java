package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    // Count failed attempts in a time window
    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.ipAddress = :ipAddress " +
            "AND la.successful = false AND la.attemptTime > :since")
    long countRecentFailedAttempts(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);
}