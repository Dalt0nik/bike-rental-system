package lt.psk.bikerental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Booking.CreateBookingDTO;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.entity.BikeState;
import lt.psk.bikerental.entity.Booking;
import lt.psk.bikerental.repository.BikeRepository;
import lt.psk.bikerental.repository.BookingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@Service
public class BookingService {

    private final BikeRepository bikeRepository;

    private final BookingRepository bookingRepository;

    private final ModelMapper modelMapper;

    public BookingDTO createBooking(CreateBookingDTO createBookingDTO) {
        Booking booking = modelMapper.map(createBookingDTO, Booking.class);
        booking.setStartTime(new Timestamp(System.currentTimeMillis()));
        booking.setActive(true);

        Bike bike = bikeRepository.findById(createBookingDTO.getBookedBikeId())
                .orElseThrow(() -> new RuntimeException("Bike not found"));

        if (!bike.getState().equals(BikeState.FREE)) {
            throw new RuntimeException("Bike is not available");
        }
        bike.setState(BikeState.BOOKED);
        bikeRepository.save(bike);

        return modelMapper.map(bookingRepository.save(booking), BookingDTO.class);
    }

    public BookingDTO getBooking(UUID id) {
        return bookingRepository.findById(id)
                .map(x -> modelMapper.map(x, BookingDTO.class))
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
    }

    public void deactivateBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id " + id));
        booking.setActive(false);
        booking.getBike().setState(BikeState.FREE);
        bikeRepository.save(booking.getBike());
        bookingRepository.save(booking);
    }
}
