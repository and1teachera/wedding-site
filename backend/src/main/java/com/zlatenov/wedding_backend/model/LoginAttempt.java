package com.zlatenov.wedding_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts",
        indexes = {
                @Index(name = "idx_attempt_time", columnList = "attempt_time"),
                @Index(name = "idx_ip_address", columnList = "ip_address")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attempt_time", nullable = false)
    private LocalDateTime attemptTime;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(nullable = false)
    private boolean successful;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "user_agent")
    private String userAgent;


    @PrePersist
    protected void onCreate() {
        if (attemptTime == null) {
            attemptTime = LocalDateTime.now();
        }
    }
}
