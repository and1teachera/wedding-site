package com.zlatenov.wedding_backend.repository;


import com.zlatenov.wedding_backend.model.Family;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Angel Zlatenov
 */

class FamilyRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private FamilyRepository familyRepository;

    @DisplayName("Should save and retrieve family")
    @Test
    void shouldSaveAndRetrieveFamily() {
        Family family = new Family();
        family.setName("Smith Family");

        familyRepository.save(family);

        Optional<Family> retrievedFamily = familyRepository.findById(family.getId());
        assertThat(retrievedFamily).isPresent();
        assertThat(retrievedFamily.get().getName()).isEqualTo("Smith Family");
    }

    @DisplayName("Should find families by name case insensitive")
    @Test
    void shouldFindByNameContainingIgnoreCase() {
        Family family1 = new Family();
        family1.setName("Smith Family");
        Family family2 = new Family();
        family2.setName("SMITH-JONES");
        familyRepository.saveAll(List.of(family1, family2));

        List<Family> families = familyRepository.findByNameContainingIgnoreCase("smith");
        assertThat(families).hasSize(2);
    }
}
