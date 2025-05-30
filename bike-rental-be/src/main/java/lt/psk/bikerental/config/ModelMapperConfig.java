package lt.psk.bikerental.config;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Bike.BikeDTO;
import lt.psk.bikerental.DTO.Bike.CreateBikeDTO;
import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Booking.CreateBookingDTO;
import lt.psk.bikerental.DTO.Trip.TripDTO;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.entity.Booking;
import lt.psk.bikerental.entity.Trip;
import lt.psk.bikerental.entity.User;
import lt.psk.bikerental.repository.BikeRepository;
import lt.psk.bikerental.repository.BikeStationRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ModelMapperConfig {
    private final BikeStationRepository bikeStationRepository;

    private final BikeRepository bikeRepository;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        // Too lax of a matching strategy mapped some values incorrectly.
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Entity-to-UUID converters
        mapper.addConverter(v -> v.getSource() != null ? v.getSource().getId() : null, Bike.class, UUID.class);
        mapper.addConverter(v -> v.getSource() != null ? v.getSource().getId() : null, Booking.class, UUID.class);
        mapper.addConverter(v -> v.getSource() != null ? v.getSource().getId() : null, Trip.class, UUID.class);
        mapper.addConverter(v -> v.getSource() != null ? v.getSource().getId() : null, User.class, UUID.class);

        // UUID-to-Entity converters
        mapper.addConverter(v -> bikeRepository.findById(v.getSource())
                        .orElseThrow(() -> new EntityNotFoundException("Bike not found")),
                UUID.class,
                Bike.class);

        // Bike-DTO specific converters, mappings
        mapper.createTypeMap(Bike.class, BikeDTO.class)
                .addMapping(Bike::getCurStation, BikeDTO::setCurStationId);

        mapper.createTypeMap(CreateBikeDTO.class, Bike.class)
                .addMapping(CreateBikeDTO::getCurrentBikeStationId, Bike::setCurStation);

        // Trip, TripDTO mapping
        mapper.createTypeMap(Trip.class, TripDTO.class)
                .addMapping(Trip::getBike, TripDTO::setBikeId)
                .addMapping(Trip::getUser, TripDTO::setUserId);

        // Booking-DTO specific converters, mappings
        mapper.createTypeMap(Booking.class, BookingDTO.class)
                .addMapping(Booking::getBike, BookingDTO::setBookedBikeId)
                .addMapping(Booking::getUser, BookingDTO::setUserId);

        mapper.createTypeMap(CreateBookingDTO.class, Booking.class)
                .addMapping(CreateBookingDTO::getBookedBikeId, Booking::setBike);

        return mapper;
    }
}
