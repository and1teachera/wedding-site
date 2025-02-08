package com.zlatenov.wedding_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResponseStatus status;

    @Column(name = "dietary_notes")
    private String dietaryNotes;

    @Column(name = "additional_notes")
    private String additionalNotes;
}