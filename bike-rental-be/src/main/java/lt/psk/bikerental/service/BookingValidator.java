package lt.psk.bikerental.service;

import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.entity.*;
import lt.psk.bikerental.exception.BikeNotAvailableException;
import lt.psk.bikerental.exception.InvalidBookingException;
import lt.psk.bikerental.repository.BookingRepository;
import lt.psk.bikerental.repository.TripRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingValidator {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;

    public void validateBookingCreation(Bike bike, User user) {
        checkBikeIsFree(bike);
        checkUserHasNoActiveBookedBikes(user);
        checkUserHasNoOngoingTrip(user);
    }

    private void checkBikeIsFree(Bike bike) {
        if (bike.getState() != BikeState.FREE) {
            throw new BikeNotAvailableException("Bike is not available for rent");
        }
    }

    private void checkUserHasNoActiveBookedBikes(User user) {
        Instant now = Instant.now();
        Optional<Booking> booking = bookingRepository.findFirstByUserIdAndStartTimeBeforeAndFinishTimeAfter(user.getId(), now, now);
        if (booking.isPresent()) {
            throw new InvalidBookingException("You have a booking for a different bike");
        }
    }

    private void checkUserHasNoOngoingTrip(User user) {
        Optional<Trip> trip = tripRepository.findTopByUserAndFinishTimeIsNull(user);
        if (trip.isPresent()) {
            throw new InvalidBookingException("You are already on a trip");
        }
    }
}
