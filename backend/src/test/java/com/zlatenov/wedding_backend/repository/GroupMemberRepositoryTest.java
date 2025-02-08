package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.GroupMember;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.model.UserGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GroupMemberRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @DisplayName("Should save group member association")
    @Test
    void shouldSaveGroupMember() {
        User user = createUser();
        UserGroup group = createGroup();
        entityManager.persist(user);
        entityManager.persist(group);

        GroupMember groupMember = new GroupMember(group, user);
        GroupMember saved = groupMemberRepository.save(groupMember);
        
        assertThat(saved.getGroup().getId()).isNotNull();
        assertThat(saved.getUser().getId()).isNotNull();
    }

    @DisplayName("Should find members by group id")
    @Test
    void shouldFindByGroupId() {
        User user = createUser();
        UserGroup group = createGroup();
        entityManager.persist(user);
        entityManager.persist(group);
        
        GroupMember groupMember = new GroupMember(group, user);
        entityManager.persist(groupMember);

        List<GroupMember> members = groupMemberRepository.findByGroupId(group.getId());
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getGroup().getId()).isEqualTo(group.getId());
    }

    @DisplayName("Should find member by user and group id")
    @Test
    void shouldFindByUserIdAndGroupId() {
        User user = createUser();
        UserGroup group = createGroup();
        entityManager.persist(user);
        entityManager.persist(group);
        
        GroupMember groupMember = new GroupMember(group, user);
        entityManager.persist(groupMember);

        Optional<GroupMember> found = groupMemberRepository
            .findByGroupIdAndUserId(group.getId(), user.getId());
        assertThat(found).isPresent();
    }

    @DisplayName("Should find all groups for user")
    @Test
    void shouldFindByUserId() {
        User user = createUser();
        UserGroup group = createGroup();
        entityManager.persist(user);
        entityManager.persist(group);
        
        GroupMember groupMember = new GroupMember(group, user);
        entityManager.persist(groupMember);

        List<GroupMember> memberships = groupMemberRepository.findByUserId(user.getId());
        assertThat(memberships).hasSize(1);
        assertThat(memberships.get(0).getUser().getId()).isEqualTo(user.getId());
    } 

    private User createUser() {
        return User.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .password("password")
            .build();
    }

    private UserGroup createGroup() {
        return UserGroup.builder()
            .groupName("Test Group")
            .build();
    }
}