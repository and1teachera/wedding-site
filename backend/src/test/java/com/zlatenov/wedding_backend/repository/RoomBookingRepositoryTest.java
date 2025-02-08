package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.BookingStatus;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.Room;
import com.zlatenov.wedding_backend.model.RoomBooking;
import com.zlatenov.wedding_backend.model.UserGroup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

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
        booking.setCheckInDate(LocalDate.now());
        booking.setCheckOutDate(LocalDate.now().plusDays(2));

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
        booking.setCheckInDate(LocalDate.now());
        booking.setCheckOutDate(LocalDate.now().plusDays(2));
        return booking;
    }
}
