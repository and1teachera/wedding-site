package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {
    List<RoomBooking> findByFamilyId(Long familyId);
    List<RoomBooking> findByGroupId(Long groupId);
    List<RoomBooking> findByStatus(com.zlatenov.wedding_backend.model.BookingStatus status);

    /**
     * Find the latest confirmed booking for a family
     * @param familyId The family Id
     * @return The latest confirmed booking or empty if none exists
     */
    @Query(value = "SELECT * FROM room_bookings rb " +
            "WHERE rb.family_id = :familyId AND rb.status = 'CONFIRMED' " +
            "ORDER BY rb.booking_time DESC LIMIT 1", nativeQuery = true)
    Optional<RoomBooking> findLatestConfirmedBookingByFamilyId(@Param("familyId") Long familyId);

    @Query(value = "SELECT * FROM room_bookings rb " +
            "WHERE rb.family_id = :familyId " +
            "ORDER BY rb.id DESC LIMIT 1", nativeQuery = true)
    Optional<RoomBooking> findLatestBookingByFamilyId(@Param("familyId") Long familyId);
    
    /**
     * Find the latest booking for a room
     * @param roomId The room Id
     * @return The latest booking for the room or empty if none exists
     */
    @Query(value = "SELECT * FROM room_bookings rb " +
            "WHERE rb.room_id = :roomId " +
            "ORDER BY rb.booking_time DESC LIMIT 1", nativeQuery = true)
    Optional<RoomBooking> findLatestBookingByRoomId(@Param("roomId") Long roomId);
}