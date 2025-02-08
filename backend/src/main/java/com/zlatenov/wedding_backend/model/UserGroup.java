package com.zlatenov.wedding_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "user_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String groupName;


    @ManyToMany(mappedBy = "groups")
    private Set<User> users;

    @OneToMany(mappedBy = "group")
    private Set<RoomBooking> bookings;

}