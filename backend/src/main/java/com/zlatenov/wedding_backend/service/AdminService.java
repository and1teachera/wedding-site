package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.FamilyCreationRequest;
import com.zlatenov.wedding_backend.dto.UserCreationRequest;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.repository.FamilyRepository;
import com.zlatenov.wedding_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final PasswordEncoder passwordEncoder;
    //TODO: just a placeholder for now - will be replaced with a more secure solution
    private final String defaultPassword = "mywedding";

    @Transactional
    public void createUser(UserCreationRequest request) {
        log.info("Creating new user: {} {}", request.getFirstName(), request.getLastName());

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(defaultPassword))
                .isChild(request.isChild())
                .isAdmin(request.isAdmin())
                .build();

        userRepository.save(user);
        log.info("Successfully created user with ID: {}", user.getId());
    }

    @Transactional
    public void createFamily(FamilyCreationRequest request) {
        log.info("Creating new family: {}", request.getFamilyName());

        // Create family
        Family family = Family.builder()
                .name(request.getFamilyName())
                .build();

        family = familyRepository.save(family);
        log.info("Created family with ID: {}", family.getId());

        // Create primary user
        User primaryUser = User.builder()
                .firstName(request.getPrimaryUser().getFirstName())
                .lastName(request.getPrimaryUser().getLastName())
                .email(request.getPrimaryUser().getEmail())
                .phone(request.getPrimaryUser().getPhone())
                .password(passwordEncoder.encode(defaultPassword))
                .isChild(false)
                .isAdmin(false)
                .family(family)
                .build();

        userRepository.save(primaryUser);
        log.info("Created primary user with ID: {}", primaryUser.getId());

        // Create family members
        for (UserCreationRequest member : request.getFamilyMembers()) {
            User familyMember = User.builder()
                    .firstName(member.getFirstName())
                    .lastName(member.getLastName())
                    .email(member.getEmail())
                    .phone(member.getPhone())
                    .password(passwordEncoder.encode(defaultPassword))
                    .isChild(member.isChild())
                    .isAdmin(false)
                    .family(family)
                    .build();

            userRepository.save(familyMember);
            log.info("Created family member with ID: {}", familyMember.getId());
        }
    }
}