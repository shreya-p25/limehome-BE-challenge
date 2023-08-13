package de.limehome.backendchallengejava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableAutoConfiguration
@EntityScan("de.limehome.backendchallengejava")
@EnableJpaRepositories("de.limehome.backendchallengejava")
public class BookingService {

    public class UnableToBook extends Exception {
        UnableToBook(String reason) {
            super(reason);
        }
    }

    @Autowired
    BookingRepository bookingRepository;

    public Booking createBooking(Booking booking) throws UnableToBook {
        verifyBookingPossible(booking);
        bookingRepository.save(booking);
        return booking;
    }

    private boolean verifyBookingPossible(Booking booking) throws UnableToBook {
        List<Booking> bookings = bookingRepository.findAll();

        // check 1 : The same guest cannot book the same unit multiple times
        long numSameBookings = bookings
                .stream()
                .filter(dbBooking -> dbBooking.guestName.equals(booking.guestName))
                .filter(dbBooking -> dbBooking.unitID.equals(booking.unitID))
                .count();
        if (numSameBookings > 0) {
            throw new UnableToBook("The given guest name cannot book the same unit multiple times");
        }

        // check 2 : The same guest cannot be in multiple units at the same time
        long numTimesAlreadyBooked = bookings
                .stream()
                .filter(dbBooking -> dbBooking.guestName.equals(booking.guestName))
                .count();
        if (numTimesAlreadyBooked > 0) {
            throw new UnableToBook("The same guest cannot be in multiple units at the same time");
        }

        // check 3 : Unit is available for the check-in date
        long numBookingsForCurrentUnit = bookings
                .stream()
                .filter(dbBooking -> dbBooking.unitID.equals(booking.unitID))
                .filter(dbBooking -> dbBooking.checkInDate.plusDays(dbBooking.numberOfNights-1).isAfter(booking.checkInDate))
                .count();
        if (numBookingsForCurrentUnit > 0) {
            throw new UnableToBook("For the given check-in date, the unit is already occupied");
        }

        return true;
    }

    public boolean extendBooking(ExtendBookingInput extendBookingInput) throws UnableToBook {

        //get Booking object for guest given guestName and unitId
        List<Booking> existingBookings = bookingRepository.findByGuestNameAndUnitID(extendBookingInput.guestName, extendBookingInput.unitID);
        if (existingBookings.isEmpty()){
            throw new UnableToBook("There is no existing booking for the given guest name and unitID");
        }

        Booking existingBooking = existingBookings.get(0);

        List<Booking> bookings = bookingRepository.findAllByCheckInDateGreaterThan(
                existingBooking.checkInDate.plusDays(existingBooking.numberOfNights-1)
        );

        //Check if the unit is available for the number of days
        long numBookingsForCurrentUnit = bookings
                .stream()
                .filter(dbBooking -> dbBooking.unitID.equals(existingBooking.unitID))
                .filter(dbBooking -> dbBooking.checkInDate.isBefore(
                        existingBooking.checkInDate.plusDays(
                                existingBooking.numberOfNights + extendBookingInput.numberOfNightsToExtend -1
                        )
                    )
                )
                .count();
        if (numBookingsForCurrentUnit > 0) {
            throw new UnableToBook("Unable to extend booking as unit is already booked for the given duration");
        }

        existingBooking.numberOfNights += extendBookingInput.numberOfNightsToExtend;
        bookingRepository.save(existingBooking);

        return true;
    }

}
