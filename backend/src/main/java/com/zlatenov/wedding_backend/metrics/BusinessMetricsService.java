package com.zlatenov.wedding_backend.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for tracking business-specific metrics
 */
@Service
public class BusinessMetricsService {

    private final MeterRegistry meterRegistry;
    private final Counter failedLoginAttempts;
    private final Counter successfulLoginAttempts;
    private final Counter rsvpSubmissions;
    private final Counter accommodationBookings;
    private final Counter mediaUploads;
    private final ConcurrentMap<String, AtomicInteger> activeUsersByRole = new ConcurrentHashMap<>();
    private final Timer rsvpProcessingTime;

    public BusinessMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Authentication metrics
        this.failedLoginAttempts = Counter.builder("wedding.auth.login.failure")
                .description("Total number of failed login attempts")
                .register(meterRegistry);
        
        this.successfulLoginAttempts = Counter.builder("wedding.auth.login.success")
                .description("Total number of successful login attempts")
                .register(meterRegistry);
        
        // Business operation metrics
        this.rsvpSubmissions = Counter.builder("wedding.rsvp.submissions")
                .description("Total number of RSVP submissions")
                .register(meterRegistry);
        
        this.accommodationBookings = Counter.builder("wedding.accommodation.bookings")
                .description("Total number of accommodation bookings")
                .register(meterRegistry);
        
        this.mediaUploads = Counter.builder("wedding.media.uploads")
                .description("Total number of media uploads")
                .register(meterRegistry);
        
        // Active users by role gauges
        initializeActiveUserGauges();
        
        // Timer for RSVP processing
        this.rsvpProcessingTime = Timer.builder("wedding.rsvp.processing.time")
                .description("Time taken to process RSVP submissions")
                .register(meterRegistry);
    }
    
    private void initializeActiveUserGauges() {
        // Initialize active users count for each role
        activeUsersByRole.put("ADMIN", new AtomicInteger(0));
        activeUsersByRole.put("USER", new AtomicInteger(0));
        activeUsersByRole.put("GUEST", new AtomicInteger(0));
        
        // Register gauges for each role
        activeUsersByRole.forEach((role, count) -> {
            Gauge.builder("wedding.users.active", count, AtomicInteger::get)
                    .tag("role", role)
                    .description("Number of active users by role")
                    .register(meterRegistry);
        });
    }
    
    // Authentication metrics methods
    public void incrementFailedLoginAttempts() {
        failedLoginAttempts.increment();
    }
    
    public void incrementSuccessfulLoginAttempts() {
        successfulLoginAttempts.increment();
    }
    
    // User activity metrics methods
    public void incrementActiveUsers(String role) {
        activeUsersByRole.computeIfPresent(role, (k, v) -> {
            v.incrementAndGet();
            return v;
        });
    }
    
    public void decrementActiveUsers(String role) {
        activeUsersByRole.computeIfPresent(role, (k, v) -> {
            v.decrementAndGet();
            return v;
        });
    }
    
    // Business operation metrics methods
    public void incrementRsvpSubmissions() {
        rsvpSubmissions.increment();
    }
    
    public void incrementAccommodationBookings() {
        accommodationBookings.increment();
    }
    
    public void incrementMediaUploads() {
        mediaUploads.increment();
    }
    
    // RSVP processing timer
    public Timer getRsvpProcessingTimer() {
        return rsvpProcessingTime;
    }
}