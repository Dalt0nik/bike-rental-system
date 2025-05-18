package lt.psk.bikerental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.entity.BikeState;
import lt.psk.bikerental.entity.Trip;
import lt.psk.bikerental.entity.TripState;
import lt.psk.bikerental.entity.User;
import lt.psk.bikerental.exception.ActiveTripExistsException;
import lt.psk.bikerental.exception.BikeNotAvailableException;
import lt.psk.bikerental.exception.InvalidBookingException;
import lt.psk.bikerental.repository.TripRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TripValidator {

    private final TripRepository tripRepository;

    public void validateStartTrip(Bike bike, User user, UUID requestedBikeId) {
        checkBikeIsFree(bike);
        checkUserHasMatchingBooking(user, requestedBikeId);
        checkUserHasNoOngoingTrip(user);
    }

    private void checkBikeIsFree(Bike bike) {
        if (bike.getState() != BikeState.FREE) {
            throw new BikeNotAvailableException("Bike is not available for rent");
        }
    }

    private void checkUserHasMatchingBooking(User user, UUID requestedBikeId) {
        Optional<Trip> booking = tripRepository.findTopByUserAndStateOrderByStartTimeDesc(user, TripState.BOOKED);
        if (booking.isPresent()) {
            UUID bookedBikeId = booking.get().getBike().getId();
            if (!bookedBikeId.equals(requestedBikeId)) {
                throw new InvalidBookingException("You have a booking for a different bike");
            }
        }
    }

    private void checkUserHasNoOngoingTrip(User user) {
        Optional<Trip> active = tripRepository.findTopByUserAndState(user, TripState.ONGOING);
        if (active.isPresent()) {
            throw new ActiveTripExistsException("You already have an active trip");
        }
    }
}