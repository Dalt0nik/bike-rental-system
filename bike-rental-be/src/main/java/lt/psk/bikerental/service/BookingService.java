package lt.psk.bikerental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Booking.CreateBookingDTO;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.entity.BikeState;
import lt.psk.bikerental.entity.Booking;
import lt.psk.bikerental.entity.User;
import lt.psk.bikerental.repository.BikeRepository;
import lt.psk.bikerental.repository.BookingRepository;
import lt.psk.bikerental.repository.UserRepository;
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
    public void deactivateBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id " + id));

        if (!booking.isActive()) {
            throw new IllegalStateException("Booking is already inactive");
        }

        Instant now = Instant.now();
        if (now.isAfter(booking.getFinishTime())) {
            throw new IllegalStateException("Booking is already inactive (time's up)"); // by this time active should be false
        }
        else if (now.isBefore(booking.getFinishTime())) {
            booking.setFinishTime(now);
        }

        booking.setActive(false);
        booking.getBike().setState(BikeState.FREE);

        bikeRepository.save(booking.getBike());
        bookingRepository.save(booking);
    }

}
