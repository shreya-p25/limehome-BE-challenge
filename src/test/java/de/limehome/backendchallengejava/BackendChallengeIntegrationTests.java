package de.limehome.backendchallengejava;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class BackendChallengeIntegrationTests {

    BookingInput GUEST_A_UNIT_1 = new BookingInput(
            "guestA", "1", LocalDate.now(), 5
    );

    BookingInput GUEST_A_UNIT_2 = new BookingInput(
            "guestA", "2", LocalDate.now(), 5
    );

    BookingInput GUEST_B_UNIT_1 = new BookingInput(
            "guestB", "1", LocalDate.now(), 5
    );

    ExtendBookingInput GUEST_A_UNIT_1_EXTEND = new ExtendBookingInput(
            "guestA", "1",  4
    );

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private BookingController controller;

    @MockBean
    private BookingRepository bookingRepository;

    static ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    @Test
    void createFreshBooking() throws Exception {
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).then(i -> i.getArguments()[0]);

        this.mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/v1/booking").
                        content(objectMapper.writeValueAsString(GUEST_A_UNIT_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void sameGuestSameUnitFails() throws Exception {
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).then(i -> i.getArguments()[0]);

        this.mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/v1/booking").
                        content(objectMapper.writeValueAsString(GUEST_A_UNIT_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.when(bookingRepository.findAll()).thenReturn(List.of(new Booking(GUEST_A_UNIT_1)));

        this.mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/v1/booking").
                        content(objectMapper.writeValueAsString(GUEST_A_UNIT_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("The given guest name cannot book the same unit multiple times"));
    }

    @Test
    void sameGuestDifferentUnit() throws Exception {
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).then(i -> i.getArguments()[0]);

        this.mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/v1/booking").
                        content(objectMapper.writeValueAsString(GUEST_A_UNIT_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.when(bookingRepository.findAll()).thenReturn(List.of(new Booking(GUEST_A_UNIT_1)));

        this.mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/v1/booking").
                        content(objectMapper.writeValueAsString(GUEST_A_UNIT_2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("The same guest cannot be in multiple units at the same time"));
    }

    @Test
    void differentGuestSameBooking() throws Exception {
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).then(i -> i.getArguments()[0]);

        this.mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/v1/booking").
                        content(objectMapper.writeValueAsString(GUEST_A_UNIT_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.when(bookingRepository.findAll()).thenReturn(List.of(new Booking(GUEST_A_UNIT_1)));

        this.mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/v1/booking").
                        content(objectMapper.writeValueAsString(GUEST_B_UNIT_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("For the given check-in date, the unit is already occupied"));
    }

    @Test
    void differentGuestsSameUnitDifferentDifferentDays() throws Exception {
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).then(i -> i.getArguments()[0]);

        // Create first booking
        this.mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/v1/booking").
                        content(objectMapper.writeValueAsString(GUEST_A_UNIT_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.when(bookingRepository.findAll()).thenReturn(List.of(new Booking(GUEST_A_UNIT_1)));

        // GuestB trying to book a unit that is already occupied
        BookingInput newGuest = new BookingInput(
                "guestB", GUEST_A_UNIT_1.unitID, GUEST_A_UNIT_1.checkInDate.plus(1, ChronoUnit.DAYS), 5
        );

        this.mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/v1/booking").
                        content(objectMapper.writeValueAsString(newGuest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("For the given check-in date, the unit is already occupied"));
    }

    @Test
    void extendBookingPossible() throws Exception {
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).then(i -> i.getArguments()[0]);

        Booking guestABooking = new Booking(GUEST_A_UNIT_1);

        Booking nonOverlappingGuest = new Booking(new BookingInput(
                "guestP", "1", LocalDate.now().plusDays(10), 5
        ));

        Mockito.when(bookingRepository.findByGuestNameAndUnitID("guestA", "1"))
                .thenReturn(List.of(guestABooking));

        Mockito.when(bookingRepository.findAllByCheckInDateGreaterThan(LocalDate.now().plusDays(4)))
                .thenReturn(List.of(nonOverlappingGuest));

        //Guest A is trying to extend the stay. It is successful as there are no other bookings for unit in the requested extension period
        this.mockMvc.perform(MockMvcRequestBuilders.
                        put("/api/v1/booking/extend").
                        content(objectMapper.writeValueAsString(GUEST_A_UNIT_1_EXTEND))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void extendBookingNotPossible() throws Exception {
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).then(i -> i.getArguments()[0]);

        Booking guestABooking = new Booking(GUEST_A_UNIT_1);

        Booking overlappingGuest = new Booking(new BookingInput(
                "guestP", "1", LocalDate.now().plusDays(7), 5
        ));

        Mockito.when(bookingRepository.findByGuestNameAndUnitID("guestA", "1"))
                .thenReturn(List.of(guestABooking));

        Mockito.when(bookingRepository.findAllByCheckInDateGreaterThan(LocalDate.now().plusDays(4)))
                .thenReturn(List.of(overlappingGuest));

        //Guest A is trying to extend the stay. It is successful as there are no other bookings for unit in the requested extension period
        this.mockMvc.perform(MockMvcRequestBuilders.
                        put("/api/v1/booking/extend").
                        content(objectMapper.writeValueAsString(GUEST_A_UNIT_1_EXTEND))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Unable to extend booking as unit is already booked for the given duration"));
    }
}
