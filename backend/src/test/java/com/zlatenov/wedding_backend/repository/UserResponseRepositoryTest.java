package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.ResponseStatus;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.model.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserResponseRepository userResponseRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("Should save and find user response by user ID")
    @Test
    void shouldSaveAndFindUserResponseByUserId() {
        // Create and save a user
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.response@example.com")
                .password("password123")
                .build();
        userRepository.save(user);

        // Create and save a user response
        UserResponse response = UserResponse.builder()
                .user(user)
                .status(ResponseStatus.YES)
                .dietaryNotes("Vegetarian")
                .additionalNotes("Coming with family")
                .build();
        userResponseRepository.save(response);

        // Find response by user ID
        Optional<UserResponse> foundResponse = userResponseRepository.findByUserId(user.getId());

        // Verify the response
        assertThat(foundResponse).isPresent();
        assertThat(foundResponse.get().getStatus()).isEqualTo(ResponseStatus.YES);
        assertThat(foundResponse.get().getDietaryNotes()).isEqualTo("Vegetarian");
        assertThat(foundResponse.get().getAdditionalNotes()).isEqualTo("Coming with family");
        assertThat(foundResponse.get().getUser().getId()).isEqualTo(user.getId());
    }

    @DisplayName("Should update existing user response")
    @Test
    void shouldUpdateExistingUserResponse() {
        // Create and save a user
        User user = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.response@example.com")
                .password("password123")
                .build();
        userRepository.save(user);

        // Create and save initial response
        UserResponse response = UserResponse.builder()
                .user(user)
                .status(ResponseStatus.MAYBE)
                .build();
        userResponseRepository.save(response);

        // Update the response
        Optional<UserResponse> savedResponse = userResponseRepository.findByUserId(user.getId());
        assertThat(savedResponse).isPresent();

        UserResponse updatedResponse = savedResponse.get();
        updatedResponse.setStatus(ResponseStatus.YES);
        updatedResponse.setDietaryNotes("No nuts");
        userResponseRepository.save(updatedResponse);

        // Verify the update
        Optional<UserResponse> afterUpdate = userResponseRepository.findByUserId(user.getId());
        assertThat(afterUpdate).isPresent();
        assertThat(afterUpdate.get().getStatus()).isEqualTo(ResponseStatus.YES);
        assertThat(afterUpdate.get().getDietaryNotes()).isEqualTo("No nuts");
    }

    @DisplayName("Should return empty when user has no response")
    @Test
    void shouldReturnEmptyWhenUserHasNoResponse() {
        // Create and save a user without response
        User user = User.builder()
                .firstName("Alex")
                .lastName("Johnson")
                .email("alex.response@example.com")
                .password("password123")
                .build();
        userRepository.save(user);

        // Try to find a non-existent response
        Optional<UserResponse> response = userResponseRepository.findByUserId(user.getId());

        // Verify response is empty
        assertThat(response).isEmpty();
    }

}