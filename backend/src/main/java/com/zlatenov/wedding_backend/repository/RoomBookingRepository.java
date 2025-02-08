package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {
    List<RoomBooking> findByFamilyId(Long familyId);
    List<RoomBooking> findByGroupId(Long groupId);
}