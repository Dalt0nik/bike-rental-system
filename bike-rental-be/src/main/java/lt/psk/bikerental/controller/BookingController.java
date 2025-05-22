package lt.psk.bikerental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Booking.CreateBookingDTO;
import lt.psk.bikerental.service.BookingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDTO createBooking(@Valid @RequestBody CreateBookingDTO createBookingDTO, @AuthenticationPrincipal Jwt jwt) {
        return bookingService.createBooking(createBookingDTO, jwt);
    }

    @GetMapping("/{id}")
    public BookingDTO getBooking(@PathVariable UUID id) {
        return bookingService.getBooking(id);
    }

    @PatchMapping("/{id}/deactivate")
    public void deactivateBooking(@PathVariable UUID id) {
        bookingService.deactivateBooking(id);
    }

}
