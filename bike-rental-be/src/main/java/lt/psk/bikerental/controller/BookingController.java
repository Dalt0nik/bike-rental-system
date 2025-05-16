package lt.psk.bikerental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Booking.CreateBookingDTO;
import lt.psk.bikerental.service.BookingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDTO createBooking(@Valid @RequestBody CreateBookingDTO createBookingDTO) {
        return bookingService.createBooking(createBookingDTO);
    }
}
