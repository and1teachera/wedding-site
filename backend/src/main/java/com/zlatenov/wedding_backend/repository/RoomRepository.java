package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(Integer roomNumber);

    List<Room> findByIsAvailableTrue();


    /**
     * Find the first available room
     * @return First available room or empty if none available
     */
    Optional<Room> findFirstByIsAvailableTrue();

}