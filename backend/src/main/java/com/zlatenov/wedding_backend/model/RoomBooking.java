package com.zlatenov.wedding_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup group;


    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;


    @PrePersist
    @PreUpdate
    private void validateBooking() {
        if ((family != null && group != null) || (family == null && group == null)) {
            throw new IllegalStateException("Either family or group must be set, but not both");
        }
    }

}