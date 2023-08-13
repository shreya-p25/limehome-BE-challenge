package de.limehome.backendchallengejava;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByGuestNameAndUnitID(String guestName, String unitID);
    List<Booking> findAllByCheckInDateGreaterThan(LocalDate guestCheckOutDate);
}
