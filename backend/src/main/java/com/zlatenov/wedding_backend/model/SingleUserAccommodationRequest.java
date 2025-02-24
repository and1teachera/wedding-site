package com.zlatenov.wedding_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "single_user_accommodation_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleUserAccommodationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SingleUserAccommodationStatus status;

    private String notes;

    @Column(name = "processed")
    private boolean processed;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup group;

    @PrePersist
    protected void onCreate() {
        requestDate = LocalDateTime.now();
        if (status == null) {
            status = SingleUserAccommodationStatus.PENDING;
        }
    }
}