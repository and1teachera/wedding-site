package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.BookingStatus;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.Room;
import com.zlatenov.wedding_backend.model.RoomBooking;
import com.zlatenov.wedding_backend.model.UserGroup;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Angel Zlatenov
 */

class RoomBookingRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RoomBookingRepository roomBookingRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("Should book room for family")
    @Test
    void shouldBookRoomForFamily() {
        Family family = new Family();
        family.setName("Johnson Family");
        family = familyRepository.save(family);

        Room room = new Room();
        room.setRoomNumber(202);
        room = roomRepository.save(room);

        RoomBooking booking = new RoomBooking();
        booking.setFamily(family);
        booking.setRoom(room);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(LocalDateTime.now());

        roomBookingRepository.save(booking);

        Optional<RoomBooking> retrievedBooking = roomBookingRepository.findById(booking.getId());
        assertThat(retrievedBooking).isPresent();
        assertThat(retrievedBooking.get().getRoom().getRoomNumber()).isEqualTo(202);
        assertThat(retrievedBooking.get().getFamily().getName()).isEqualTo("Johnson Family");
    }

    @DisplayName("Should find bookings by family id")
    @Test
    void shouldFindByFamilyId() {
        Family family = new Family();
        family.setName("Test Family");
        familyRepository.save(family);

        RoomBooking booking = createBooking(family, null);
        roomBookingRepository.save(booking);

        List<RoomBooking> bookings = roomBookingRepository.findByFamilyId(family.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getFamily().getId()).isEqualTo(family.getId());
    }

    @DisplayName("Should find latest confirmed booking by family ID")
    @Test
    void shouldFindLatestConfirmedBookingByFamilyId() {
        // Create and save a family
        Family family = Family.builder()
                .name("Johnson Family")
                .build();
        familyRepository.save(family);

        // Create and save rooms
        Room room1 = Room.builder()
                .roomNumber(201)
                .isAvailable(false)
                .build();
        Room room2 = Room.builder()
                .roomNumber(202)
                .isAvailable(false)
                .build();
        roomRepository.save(room1);
        roomRepository.save(room2);

        // Create older booking
        RoomBooking olderBooking = RoomBooking.builder()
                .room(room1)
                .family(family)
                .status(BookingStatus.CONFIRMED)
                .bookingTime(LocalDateTime.now().minusDays(5))
                .notes("Earlier booking")
                .build();
        roomBookingRepository.save(olderBooking);

        // Create newer booking
        RoomBooking newerBooking = RoomBooking.builder()
                .room(room2)
                .family(family)
                .status(BookingStatus.CONFIRMED)
                .bookingTime(LocalDateTime.now().minusDays(1))
                .notes("Latest booking")
                .build();
        roomBookingRepository.save(newerBooking);

        // Flush to ensure all entities are persisted
        entityManager.flush();

        // Find latest confirmed booking
        Optional<RoomBooking> latestBooking =
                roomBookingRepository.findLatestConfirmedBookingByFamilyId(family.getId());

        // Verify latest booking is returned
        assertThat(latestBooking).isPresent();
        assertThat(latestBooking.get().getNotes()).isEqualTo("Latest booking");
        assertThat(latestBooking.get().getRoom().getRoomNumber()).isEqualTo(202);
    }

    @DisplayName("Should find latest booking regardless of status")
    @Test
    void shouldFindLatestBookingRegardlessOfStatus() {
        // Create and save a family
        Family family = Family.builder()
                .name("Smith Family")
                .build();
        familyRepository.save(family);

        // Create and save rooms
        Room room1 = Room.builder()
                .roomNumber(301)
                .isAvailable(false)
                .build();
        Room room2 = Room.builder()
                .roomNumber(302)
                .isAvailable(true)
                .build();
        roomRepository.save(room1);
        roomRepository.save(room2);

        // Create confirmed booking (older)
        RoomBooking confirmedBooking = RoomBooking.builder()
                .room(room1)
                .family(family)
                .status(BookingStatus.CONFIRMED)
                .bookingTime(LocalDateTime.now().minusDays(3))
                .notes("Confirmed booking")
                .build();
        roomBookingRepository.save(confirmedBooking);

        // Create cancelled booking (newer)
        RoomBooking cancelledBooking = RoomBooking.builder()
                .room(room2)
                .family(family)
                .status(BookingStatus.CANCELLED)
                .bookingTime(LocalDateTime.now().minusDays(1))
                .notes("Cancelled booking")
                .build();
        roomBookingRepository.save(cancelledBooking);

        // Flush to ensure all entities are persisted
        entityManager.flush();

        // Find latest booking
        Optional<RoomBooking> latestBooking =
                roomBookingRepository.findLatestBookingByFamilyId(family.getId());

        // Verify cancelled (but newer) booking is returned
        assertThat(latestBooking).isPresent();
        assertThat(latestBooking.get().getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(latestBooking.get().getNotes()).isEqualTo("Cancelled booking");
    }

    @DisplayName("Should return empty when family has no bookings")
    @Test
    void shouldReturnEmptyWhenFamilyHasNoBookings() {
        // Create and save a family without bookings
        Family family = Family.builder()
                .name("No Bookings Family")
                .build();
        familyRepository.save(family);

        // Try to find non-existent bookings
        List<RoomBooking> bookings = roomBookingRepository.findByFamilyId(family.getId());
        Optional<RoomBooking> latestConfirmedBooking =
                roomBookingRepository.findLatestConfirmedBookingByFamilyId(family.getId());
        Optional<RoomBooking> latestBooking =
                roomBookingRepository.findLatestBookingByFamilyId(family.getId());

        // Verify all are empty
        assertThat(bookings).isEmpty();
        assertThat(latestConfirmedBooking).isEmpty();
        assertThat(latestBooking).isEmpty();
    }

    @DisplayName("Should find bookings by group id")
    @Test
    void shouldFindByGroupId() {
        UserGroup group = new UserGroup();
        group.setGroupName("Test Group");
        userGroupRepository.save(group);

        RoomBooking booking = createBooking(null, group);
        roomBookingRepository.save(booking);

        List<RoomBooking> bookings = roomBookingRepository.findByGroupId(group.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getGroup().getId()).isEqualTo(group.getId());
    }

    private RoomBooking createBooking(Family family, UserGroup group) {
        RoomBooking booking = new RoomBooking();
        booking.setFamily(family);
        booking.setGroup(group);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(LocalDateTime.now());
        return booking;
    }
}
