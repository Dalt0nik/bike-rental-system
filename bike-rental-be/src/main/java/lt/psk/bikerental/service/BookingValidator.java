package lt.psk.bikerental.service;

import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.entity.BikeState;
import lt.psk.bikerental.entity.Booking;
import lt.psk.bikerental.entity.User;
import lt.psk.bikerental.exception.BikeNotAvailableException;
import lt.psk.bikerental.exception.InvalidBookingException;
import lt.psk.bikerental.repository.BookingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingValidator {

    private final BookingRepository bookingRepository;

    public void validateBookingCreation(Bike bike, User user) {
        checkBikeIsFree(bike);
        checkUserHasNoActiveBookedBikes(user);
        // checkUserHasNoOngoingTrip(user); // TO DO when Trip is merged
    }

    private void checkBikeIsFree(Bike bike) {
        if (bike.getState() != BikeState.FREE) {
            throw new BikeNotAvailableException("Bike is not available for rent");
        }
    }

    private void checkUserHasNoActiveBookedBikes(User user) {
        Optional<Booking> booking = bookingRepository.findFirstByUserIdAndIsActiveTrue(user.getId());
        if (booking.isPresent()) {
            throw new InvalidBookingException("You have a booking for a different bike");
        }
    }

    // TO DO when Trip is merged
//    private void checkUserHasNoOngoingTrip(User user) {
//    }
}
