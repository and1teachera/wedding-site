package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RoomRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoomRepository roomRepository;

    @DisplayName("Should save room with valid data")
    @Test
    void shouldSaveRoom() {
        Room room = Room.builder()
            .roomNumber(101)
            .isAvailable(true)
            .build();

        Room savedRoom = roomRepository.save(room);
        assertThat(savedRoom.getId()).isNotNull();
    }

    @DisplayName("Should find room by room number")
    @Test
    void shouldFindByRoomNumber() {
        Room room = Room.builder()
            .roomNumber(101)
            .isAvailable(true)
            .build();
        entityManager.persist(room);

        Optional<Room> found = roomRepository.findByRoomNumber(101);
        assertThat(found).isPresent();
        assertThat(found.get().getRoomNumber()).isEqualTo(101);
    }

    @DisplayName("Should find all available rooms")
    @Test
    void shouldFindAvailableRooms() {
        Room availableRoom = Room.builder()
            .roomNumber(101)
            .isAvailable(true)
            .build();
        Room unavailableRoom = Room.builder()
            .roomNumber(102)
            .isAvailable(false)
            .build();
        entityManager.persist(availableRoom);
        entityManager.persist(unavailableRoom);

        List<Room> availableRooms = roomRepository.findByIsAvailableTrue();
        assertThat(availableRooms).hasSize(1);
        assertThat(availableRooms.get(0).isAvailable()).isTrue();
    }


}