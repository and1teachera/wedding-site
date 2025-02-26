package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author Angel Zlatenov
 */

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user;
        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + identifier));
        } else {
            String[] names = identifier.split(" ");
            if (names.length != 2) {
                throw new UsernameNotFoundException("Invalid name format");
            }
            user = userRepository.findByFirstNameAndLastName(names[0], names[1])
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with name: " + identifier));
        }

        return new org.springframework.security.core.userdetails.User(
                identifier,
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.isAdmin() ? "ADMIN" : "GUEST"))
        );
    }
}
