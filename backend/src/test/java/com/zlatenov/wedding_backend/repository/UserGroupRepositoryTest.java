package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.model.UserGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Angel Zlatenov
 */

class UserGroupRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("Should save group with members")
    @Test
    void shouldSaveGroupWithMembers() {
        User user1 = new User();
        user1.setFirstName("Alice");
        user1.setLastName("Brown");
        user1.setEmail("alice.brown@example.com");
        user1.setPassword("password123");

        User user2 = new User();
        user2.setFirstName("Bob");
        user2.setLastName("Green");
        user2.setEmail("bob.green@example.com");
        user2.setPassword("password123");

        userRepository.saveAll(Set.of(user1, user2));

        UserGroup group = new UserGroup();
        group.setGroupName("Friends Group");
        group.setUsers(Set.of(user1, user2));

        userGroupRepository.save(group);

        Optional<UserGroup> retrievedGroup = userGroupRepository.findById(group.getId());
        assertThat(retrievedGroup).isPresent();
        assertThat(retrievedGroup.get().getGroupName()).isEqualTo("Friends Group");
        assertThat(retrievedGroup.get().getUsers()).hasSize(2);
    }

    @Test
    void shouldFindByUsersId() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password123");
        user.setEmail("test@test.com");
        userRepository.save(user);

        UserGroup group = new UserGroup();
        group.setGroupName("Test Group");
        group.setUsers(Set.of(user));
        userGroupRepository.save(group);

        List<UserGroup> groups = userGroupRepository.findByUsers_Id(user.getId());
        assertThat(groups).hasSize(1);
        assertThat(groups.getFirst().getGroupName()).isEqualTo("Test Group");
    }
}
