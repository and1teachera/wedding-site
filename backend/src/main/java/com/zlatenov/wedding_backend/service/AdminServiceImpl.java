package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.FamilyCreationRequest;
import com.zlatenov.wedding_backend.dto.UserCreationRequest;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.repository.FamilyRepository;
import com.zlatenov.wedding_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${DEFAULT_PASSWORD}")
    private String defaultPassword;

    @Override
    public void createUser(UserCreationRequest request) {
        log.info("Creating new user: {} {}", request.getFirstName(), request.getLastName());

        User user = createUserFromUserCreationRequest(request, null);

        userRepository.save(user);
        log.info("Successfully created user with Id: {}", user.getId());
    }

    @Transactional
    @Override
    public void createFamily(FamilyCreationRequest request) {
        log.info("Creating new family: {}", request.getFamilyName());

        Family family = Family.builder()
                .name(request.getFamilyName())
                .build();

        family = familyRepository.save(family);
        log.info("Created family with Id: {}", family.getId());

        User primaryUser = createUserFromUserCreationRequest(request.getPrimaryUser(), family);

        userRepository.save(primaryUser);
        log.info("Created primary user with Id: {}", primaryUser.getId());

        createFamilyMembers(request, family);
    }

    private User createUserFromUserCreationRequest(UserCreationRequest userCreationRequest, Family family) {
        return User.builder()
                .firstName(userCreationRequest.getFirstName())
                .lastName(userCreationRequest.getLastName())
                .email(userCreationRequest.getEmail())
                .phone(userCreationRequest.getPhone())
                .password(passwordEncoder.encode(defaultPassword))
                .isChild(userCreationRequest.isChild())
                .isAdmin(false)
                .family(family)
                .build();
    }

    private void createFamilyMembers(FamilyCreationRequest request, Family family) {
        for (UserCreationRequest member : request.getFamilyMembers()) {
            User familyMember = createUserFromUserCreationRequest(member, family);

            userRepository.save(familyMember);
            log.info("Created family member with Id: {}", familyMember.getId());
        }
    }
}