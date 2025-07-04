package lt.psk.bikerental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Booking.CreateBookingDTO;
import lt.psk.bikerental.entity.*;
import lt.psk.bikerental.repository.BikeRepository;
import lt.psk.bikerental.repository.BookingRepository;
import lt.psk.bikerental.repository.UserRepository;
import lt.psk.bikerental.service.ws.WsEventSendingService;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Service
public class BookingService {

    private final BikeRepository bikeRepository;

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final BookingValidator bookingValidator;
    private final CheckService checkService;
    private final WsEventSendingService wsEventSendingService;

    @Transactional
    public BookingDTO createBooking(CreateBookingDTO createBookingDTO, Jwt jwt) {
        Bike bike = bikeRepository.findById(createBookingDTO.getBookedBikeId())
                .orElseThrow(() -> new RuntimeException("Bike not found"));

        String auth0Id = jwt.getSubject();
        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        bookingValidator.validateBookingCreation(bike, user);

        bike.setState(BikeState.BOOKED);
        bikeRepository.save(bike);
        wsEventSendingService.sendStationUpdated(bike.getCurStation());

        Booking booking = new Booking();
        booking.setBike(bike);
        booking.setUser(user);

        // TODO: prideti bikeStationId i DTO (as sakyciau kad ant create nereikia nieko grazinti)

        return modelMapper.map(bookingRepository.save(booking), BookingDTO.class);
    }

    public BookingDTO getBooking(UUID id) {
        return bookingRepository.findById(id)
                .map(x -> modelMapper.map(x, BookingDTO.class))
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
    }

    @Transactional
    public void deactivateBooking(UUID id, Jwt jwt) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id " + id));

        String auth0Id = jwt.getSubject();
        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Instant now = Instant.now();
        if (now.isAfter(booking.getFinishTime())) {
            throw new IllegalStateException("Booking is already inactive (time's up)");
        }
        if (now.isBefore(booking.getFinishTime())) {
            booking.setFinishTime(now);
        }

        booking.getBike().setState(BikeState.FREE);

        bikeRepository.save(booking.getBike());
        wsEventSendingService.sendStationUpdated(booking.getBike().getCurStation());

        bookingRepository.save(booking);

        checkService.createAndSaveCheck(user, booking, null);
    }

    @Transactional
    public void deactivateBookingByTrip(Booking booking, Trip trip) {
        booking.setFinishTime(trip.getStartTime());
        bookingRepository.save(booking);
    }
}
