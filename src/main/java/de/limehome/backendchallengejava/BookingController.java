package de.limehome.backendchallengejava;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

    @Autowired
    BookingService bookingService;

    @Data
    private class HealthResponse {
        String message = "OK";
    }

    @GetMapping("/")
    public HealthResponse helloWorld() {
        return new HealthResponse();
    }

    @PostMapping("/api/v1/booking")
    public ResponseEntity<Object> createBooking(@RequestBody BookingInput bookingInput) {
        try {
            Booking booking = new Booking(bookingInput.guestName, bookingInput.unitID, bookingInput.checkInDate, bookingInput.numberOfNights);
            return ResponseEntity.ok(bookingService.createBooking(booking));
        } catch (BookingService.UnableToBook bookingException) {
            return ResponseEntity.badRequest().body(bookingException.getMessage());
        }
    }
}
