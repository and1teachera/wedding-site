package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.SingleUserAccommodationRequest;
import com.zlatenov.wedding_backend.model.SingleUserAccommodationStatus;
import com.zlatenov.wedding_backend.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SingleUserAccommodationRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private SingleUserAccommodationRepository accommodationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("Should find latest accommodation request by user ID")
    @Test
    void shouldFindLatestAccommodationRequestByUserId() {
        // Create and save a user
        User user = User.builder()
                .firstName("Sam")
                .lastName("Wilson")
                .email("sam.wilson@example.com")
                .password("password123")
                .build();
        userRepository.save(user);

        // Create older request
        SingleUserAccommodationRequest olderRequest = SingleUserAccommodationRequest.builder()
                .user(user)
                .status(SingleUserAccommodationStatus.PENDING)
                .requestDate(LocalDateTime.now().minusDays(5))
                .notes("First request")
                .build();
        accommodationRepository.save(olderRequest);

        // Create newer request
        SingleUserAccommodationRequest newerRequest = SingleUserAccommodationRequest.builder()
                .user(user)
                .status(SingleUserAccommodationStatus.PENDING)
                .requestDate(LocalDateTime.now().minusDays(1))
                .notes("Latest request")
                .build();
        accommodationRepository.save(newerRequest);

        // Flush to ensure all entities are persisted
        entityManager.flush();

        // Find latest request
        Optional<SingleUserAccommodationRequest> latestRequest = accommodationRepository.findLatestByUserId(user.getId());

        // Verify latest request is returned
        assertThat(latestRequest).isPresent();
        assertThat(latestRequest.get().getNotes()).isEqualTo("Latest request");
    }

    @DisplayName("Should find latest pending accommodation request by user ID")
    @Test
    void shouldFindLatestPendingAccommodationRequestByUserId() {
        // Create and save a user
        User user = User.builder()
                .firstName("Kate")
                .lastName("Brown")
                .email("kate.brown@example.com")
                .password("password123")
                .build();
        userRepository.save(user);

        // Create cancelled request (newer)
        SingleUserAccommodationRequest cancelledRequest = SingleUserAccommodationRequest.builder()
                .user(user)
                .status(SingleUserAccommodationStatus.CANCELLED)
                .requestDate(LocalDateTime.now().minusDays(1))
                .notes("Cancelled request")
                .build();
        accommodationRepository.save(cancelledRequest);

        // Create pending request (older but still pending)
        SingleUserAccommodationRequest pendingRequest = SingleUserAccommodationRequest.builder()
                .user(user)
                .status(SingleUserAccommodationStatus.PENDING)
                .requestDate(LocalDateTime.now().minusDays(3))
                .notes("Pending request")
                .build();
        accommodationRepository.save(pendingRequest);

        // Flush to ensure all entities are persisted
        entityManager.flush();

        // Find latest pending request
        Optional<SingleUserAccommodationRequest> latestPendingRequest =
                accommodationRepository.findLatestPendingByUserId(user.getId());

        // Verify pending request is returned despite being older
        assertThat(latestPendingRequest).isPresent();
        assertThat(latestPendingRequest.get().getStatus()).isEqualTo(SingleUserAccommodationStatus.PENDING);
        assertThat(latestPendingRequest.get().getNotes()).isEqualTo("Pending request");
    }

    @DisplayName("Should return empty when user has no accommodation requests")
    @Test
    void shouldReturnEmptyWhenUserHasNoAccommodationRequests() {
        // Create and save a user without accommodation requests
        User user = User.builder()
                .firstName("Mark")
                .lastName("Johnson")
                .email("mark.johnson@example.com")
                .password("password123")
                .build();
        userRepository.save(user);

        // Try to find non-existent requests
        Optional<SingleUserAccommodationRequest> latestRequest =
                accommodationRepository.findLatestByUserId(user.getId());
        Optional<SingleUserAccommodationRequest> latestPendingRequest =
                accommodationRepository.findLatestPendingByUserId(user.getId());

        // Verify both are empty
        assertThat(latestRequest).isEmpty();
        assertThat(latestPendingRequest).isEmpty();
    }
}