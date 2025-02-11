package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author Angel Zlatenov
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Should load user details when valid email is provided")
    void shouldLoadUserDetailsByEmail() {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .isAdmin(false)
                .build();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService
                .loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
    @DisplayName("Should load user details when valid first and last names are provided")
    void shouldLoadUserDetailsByNames() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .isAdmin(false)
                .build();

        when(userRepository.findByFirstNameAndLastName("John", "Doe"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService
                .loadUserByUsername("John Doe");

        assertNotNull(userDetails);
        assertEquals("John Doe", userDetails.getUsername());
    }

    @Test
    @DisplayName("Should throw exception when email is not found")
    void shouldThrowExceptionForInvalidEmail() {
        when(userRepository.findByEmail("invalid@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("invalid@example.com"));
    }

}