package com.zlatenov.wedding_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_child", nullable = false)
    private boolean isChild;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    @ManyToMany
    @JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<UserGroup> groups;

    @OneToOne(mappedBy = "user")
    private UserResponse response;

}