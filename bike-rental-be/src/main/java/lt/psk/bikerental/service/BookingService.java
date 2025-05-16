package lt.psk.bikerental.service;

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

@AllArgsConstructor
@Service
public class BookingService {

    private final BikeRepository bikeRepository;

    private final BookingRepository bookingRepository;

    private final ModelMapper modelMapper;

    public BookingDTO createBooking(CreateBookingDTO createBookingDTO) {
        Booking booking = modelMapper.map(createBookingDTO, Booking.class);

        Bike bike = bikeRepository.findById(createBookingDTO.getBookedBikeId())
                .orElseThrow(() -> new RuntimeException("Bike not found"));

        if (!bike.getState().equals(BikeState.FREE)) {
            throw new RuntimeException("Bike is not available");
        }
        bike.setState(BikeState.BOOKED);
        bikeRepository.save(bike);

        return modelMapper.map(bookingRepository.save(booking), BookingDTO.class);
    }
}
