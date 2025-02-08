package com.zlatenov.wedding_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "waiting_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaitingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    private boolean notificationSent;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup group;

    @PrePersist
    @PreUpdate
    private void validateRequest() {
        if ((family != null && group != null) || (family == null && group == null)) {
            throw new IllegalStateException("Either family or group must be set, but not both");
        }
    }

}