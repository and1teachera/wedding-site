package com.zlatenov.wedding_backend.repository;

/**
 *  @author Angel Zlatenov
 */

import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("Should save and retrieve user with valid data")
    @Test
    void shouldSaveAndRetrieveUser() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");

        userRepository.save(user);

        Optional<User> retrievedUser = userRepository.findByEmail("john.doe@example.com");
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getFirstName()).isEqualTo("John");
        assertThat(retrievedUser.get().getLastName()).isEqualTo("Doe");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("password123");
    }

    @DisplayName("Should update existing user details")
    @Test
    void shouldUpdateExistingUser() {
        // Create and save the initial user
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("jane.smith@example.com");
        user.setPassword("initialPassword");

        userRepository.save(user);

        // Retrieve and update the user
        Optional<User> retrievedUser = userRepository.findByEmail("jane.smith@example.com");
        assertThat(retrievedUser).isPresent();

        User updatedUser = retrievedUser.get();
        updatedUser.setLastName("Doe");
        updatedUser.setPassword("newPassword123");
        userRepository.save(updatedUser);

        // Verify the update
        Optional<User> updatedResult = userRepository.findByEmail("jane.smith@example.com");
        assertThat(updatedResult).isPresent();
        assertThat(updatedResult.get().getLastName()).isEqualTo("Doe");
        assertThat(updatedResult.get().getPassword()).isEqualTo("newPassword123");
    }

    @DisplayName("Should delete user")
    @Test
    void shouldDeleteUser() {
        // Create and save the user
        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Johnson");
        user.setEmail("alice.johnson@example.com");
        user.setPassword("password123");

        userRepository.save(user);

        // Verify user exists
        Optional<User> existingUser = userRepository.findByEmail("alice.johnson@example.com");
        assertThat(existingUser).isPresent();

        // Delete the user
        userRepository.delete(existingUser.get());

        // Verify user is deleted
        Optional<User> deletedUser = userRepository.findByEmail("alice.johnson@example.com");
        assertThat(deletedUser).isNotPresent();
    }

    @DisplayName("Should find users by family id")
    @Test
    void shouldFindByFamilyId() {
        Family family = new Family();
        family.setName("Test Family");
        entityManager.persist(family);

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@test.com");
        user.setFamily(family);
        user.setPassword("password123");
        userRepository.save(user);

        List<User> users = userRepository.findByFamilyId(family.getId());
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getFamily().getName()).isEqualTo("Test Family");
    }

}
