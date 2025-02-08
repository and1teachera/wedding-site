package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.UserGroup;
import com.zlatenov.wedding_backend.model.WaitingList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * @author Angel Zlatenov
 */

@DisplayName("Waiting List Repository Tests")
class WaitingListRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private WaitingListRepository waitingListRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @DisplayName("Should add group to waiting list")
    @Test
    void shouldAddGroupToWaitingList() {
        UserGroup group = new UserGroup();
        group.setGroupName("Late Bookers");
        group = userGroupRepository.save(group);

        WaitingList waitingListEntry = new WaitingList();
        waitingListEntry.setGroup(group);
        waitingListEntry.setRequestDate(LocalDateTime.now());

        waitingListRepository.save(waitingListEntry);

        Optional<WaitingList> retrievedEntry = waitingListRepository.findById(waitingListEntry.getId());
        assertThat(retrievedEntry).isPresent();
        assertThat(retrievedEntry.get().getGroup().getGroupName()).isEqualTo("Late Bookers");
    }

    @DisplayName("Should find entries by family ID")
    @Test
    void shouldFindByFamilyId() {
        Family family = new Family();
        family.setName("Test Family");
        familyRepository.save(family);

        WaitingList entry = new WaitingList();
        entry.setFamily(family);
        entry.setRequestDate(LocalDateTime.now());
        waitingListRepository.save(entry);

        List<WaitingList> entries = waitingListRepository.findByFamilyId(family.getId());
        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).getFamily().getId()).isEqualTo(family.getId());
    }

    @DisplayName("Should find entries by group ID")
    @Test
    void shouldFindByGroupId() {
        UserGroup group = new UserGroup();
        group.setGroupName("Test Group");
        userGroupRepository.save(group);

        WaitingList entry = new WaitingList();
        entry.setGroup(group);
        entry.setRequestDate(LocalDateTime.now());
        waitingListRepository.save(entry);

        List<WaitingList> entries = waitingListRepository.findByGroupId(group.getId());
        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).getGroup().getId()).isEqualTo(group.getId());
    }
}
