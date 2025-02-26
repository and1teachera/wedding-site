package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.exception.ResourceNotFoundException;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.ResponseStatus;
import com.zlatenov.wedding_backend.repository.FamilyRepository;
import com.zlatenov.wedding_backend.repository.UserResponseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Angel Zlatenov
 */

@AllArgsConstructor
@Service
public class FamilyServiceImpl implements FamilyService {

    private final FamilyRepository familyRepository;
    private final UserResponseRepository userResponseRepository;

    @Override
    public Family getFamilyById(Long familyId) {
        return familyRepository.findById(familyId)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found"));
    }

    @Override
    public boolean hasFamilyMembersAttending(Family family) {
        return family.getMembers().stream()
                .anyMatch(member -> userResponseRepository.findByUserId(member.getId())
                        .filter(response -> response.getStatus() == ResponseStatus.YES)
                        .isPresent());
    }
}
