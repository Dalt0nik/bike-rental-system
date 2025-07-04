package lt.psk.bikerental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Trip.CreateTripDTO;
import lt.psk.bikerental.DTO.Trip.TripDTO;
import lt.psk.bikerental.entity.*;
import lt.psk.bikerental.repository.BikeRepository;
import lt.psk.bikerental.repository.BookingRepository;
import lt.psk.bikerental.repository.TripRepository;
import lt.psk.bikerental.repository.UserRepository;
import lt.psk.bikerental.service.ws.WsEventSendingService;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static lt.psk.bikerental.entity.BikeState.FREE;
import static lt.psk.bikerental.entity.BikeState.IN_USE;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final BikeRepository bikeRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final ModelMapper mapper;
    private final TripValidator tripValidator;
    private final CheckService checkService;
    private final WsEventSendingService wsEventSendingService;

    @Transactional
    public TripDTO startTrip(CreateTripDTO dto, Jwt jwt) {
        Bike bike = bikeRepository.findById(dto.getBikeId())
                .orElseThrow(() -> new EntityNotFoundException("Bike not found"));

        String auth0Id = jwt.getSubject();
        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        tripValidator.validateStartTrip(bike, user, dto.getBikeId());

        Trip trip = new Trip();
        trip.setBike(bike);
        trip.setUser(user);

        Trip saved = tripRepository.save(trip);

        // deactivate booking
        Booking booking = bookingRepository.findFirstByBikeIdAndStartTimeBeforeAndFinishTimeAfter(
                bike.getId(), trip.getStartTime(), trip.getStartTime())
                .orElse(null);
        if (booking != null) {
            bookingService.deactivateBookingByTrip(booking, trip);
            trip.setBooking(booking);
        }

        // remove bike from station
        BikeStation oldStation = bike.getCurStation();
        bike.setCurStation(null);
        bike.setState(IN_USE);
        wsEventSendingService.sendStationUpdated(oldStation);

        return mapper.map(saved, TripDTO.class);
    }

    @Transactional
    public void endTrip(UUID tripId, Jwt jwt) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        String auth0Id = jwt.getSubject();
        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        tripValidator.validateEndTrip(trip, user);

        Instant now = Instant.now();
        trip.setFinishTime(now);

        trip.getBike().setState(FREE);

        bikeRepository.save(trip.getBike());
        wsEventSendingService.sendStationUpdated(trip.getBike().getCurStation());

        tripRepository.save(trip);

        checkService.createAndSaveCheck(user, trip.getBooking(), trip);
    }

    public TripDTO getTrip(UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        return mapper.map(trip, TripDTO.class);
    }
}
