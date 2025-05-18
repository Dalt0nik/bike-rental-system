package lt.psk.bikerental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Trip.CreateTripDTO;
import lt.psk.bikerental.DTO.Trip.TripDTO;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.entity.Trip;
import lt.psk.bikerental.entity.User;
import lt.psk.bikerental.repository.BikeRepository;
import lt.psk.bikerental.repository.TripRepository;
import lt.psk.bikerental.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final BikeRepository bikeRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Transactional
    public TripDTO startTrip(CreateTripDTO dto, Jwt jwt) {
        Bike bike = bikeRepository.findById(dto.getBikeId())
                .orElseThrow(() -> new EntityNotFoundException("Bike not found"));

        String auth0Id = jwt.getSubject();
        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        // check if user has a booking, then booked bike id should match desired bike, otherwise abort

        // check if user already has rent bike then abort

        // check if bike is free, otherwise abort


        Trip trip = new Trip();
        trip.setBike(bike);
        trip.setUser(user);

        Trip saved = tripRepository.save(trip);

        // remove bike from station
        bike.setCurStation(null);

        return mapper.map(saved, TripDTO.class);
    }
}
