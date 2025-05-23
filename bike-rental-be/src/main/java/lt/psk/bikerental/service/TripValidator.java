package lt.psk.bikerental.service;

import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.entity.BikeState;
import lt.psk.bikerental.entity.Booking;
import lt.psk.bikerental.entity.Trip;
import lt.psk.bikerental.entity.User;
import lt.psk.bikerental.exception.ActiveTripExistsException;
import lt.psk.bikerental.exception.BikeNotAvailableException;
import lt.psk.bikerental.exception.InvalidBookingException;
import lt.psk.bikerental.repository.BookingRepository;
import lt.psk.bikerental.repository.TripRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TripValidator {

    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;

    public void validateStartTrip(Bike bike, User user, UUID requestedBikeId) {
        checkUserHasNoOngoingTrip(user);
        checkUserHasNoOtherOngoingBooking(user, requestedBikeId);
        checkBikeIsNotInUse(bike);
        checkBikeIsNotBookedByAnotherUser(bike, user);
    }

    private void checkBikeIsNotInUse(Bike bike) {
        if (bike.getState() == BikeState.IN_USE) {
            throw new BikeNotAvailableException("Bike is already in use");
        }
    }

    private void checkBikeIsNotBookedByAnotherUser(Bike bike, User currentUser) {
        Instant now = Instant.now();

        Optional<Booking> booking = bookingRepository.findFirstByBikeIdAndIsActiveTrueAndStartTimeBeforeAndFinishTimeAfter(
                bike.getId(), now, now
        );

        if (booking.isPresent() && !booking.get().getUser().getId().equals(currentUser.getId())) {
            throw new BikeNotAvailableException("This bike is currently booked by another user");
        }
    }

    private void checkUserHasNoOtherOngoingBooking(User user, UUID requestedBikeId) {
        Instant now = Instant.now();

        Optional<Booking> conflictingBooking = bookingRepository.findFirstByUserIdAndIsActiveTrueAndStartTimeBeforeAndFinishTimeAfter(
                user.getId(), now, now
        );

        if (conflictingBooking.isPresent() && !conflictingBooking.get().getBike().getId().equals(requestedBikeId)) {
            throw new InvalidBookingException("You have an active booking for a different bike");
        }
    }

    private void checkUserHasNoOngoingTrip(User user) {
        Optional<Trip> activeTrip = tripRepository.findTopByUserAndFinishTimeIsNull(user);
        if (activeTrip.isPresent()) {
            throw new ActiveTripExistsException("You already have an active trip");
        }
    }
}
