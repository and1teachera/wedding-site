package com.zlatenov.wedding_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "families")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;


    @OneToMany(mappedBy = "family")
    private Set<User> members;

    @OneToMany(mappedBy = "family")
    private Set<RoomBooking> bookings;

    // getters, setters, constructors
}