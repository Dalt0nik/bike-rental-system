package lt.psk.bikerental.config;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Bike.BikeDTO;
import lt.psk.bikerental.DTO.Bike.CreateBikeDTO;
import lt.psk.bikerental.DTO.BikeStation.BikeStationDTO;
import lt.psk.bikerental.DTO.BikeStation.BikeStationPreviewDTO;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.entity.BikeState;
import lt.psk.bikerental.entity.BikeStation;
import lt.psk.bikerental.entity.Trip;
import lt.psk.bikerental.DTO.Trip.TripDTO;

import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Booking.CreateBookingDTO;
import lt.psk.bikerental.entity.*;
import lt.psk.bikerental.repository.BikeRepository;
import lt.psk.bikerental.repository.BikeStationRepository;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ModelMapperConfig {
    private final BikeStationRepository bikeStationRepository;

    private final BikeRepository bikeRepository;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Entity-ID converters
        Converter<BikeStation, UUID> bikeStationUUIDConverter = v ->
                v.getSource() != null ? v.getSource().getId() : null;
        Converter<UUID, BikeStation> uuidBikeStationConverter = v -> bikeStationRepository
                .findById(v.getSource())
                .orElseThrow(() -> new EntityNotFoundException("Station not found"));

        Converter<Bike, UUID> bikeUUIDConverter = v -> v.getSource().getId();
        Converter<UUID, Bike> uuidBikeConverter = v -> bikeRepository
                .findById(v.getSource())
                .orElseThrow(() -> new EntityNotFoundException("Bike not found"));

        Converter<User, UUID> userUUIDConverter = v -> v.getSource().getId();

        // BikeStation-DTO specific converters, mappings
        Converter<BikeStation, Integer> bikeStationFreeBikesConverter = v -> (int)v.getSource().getBikes().stream()
                .filter(x -> x.getState() == BikeState.FREE)
                .count();

        Converter<BikeStation, Integer> bikeStationFreeCapacityConverter = v -> v.getSource().getCapacity() - v.getSource().getBikes().size();

        Converter<List<Bike>, List<BikeDTO>> bikeStationBikesToBikeDTOsConverter = v -> v.getSource() != null
                ? v.getSource().stream().map(x -> new BikeDTO(x.getId(), x.getCurStation().getId(), x.getState())).toList()
                : new LinkedList<>();

        mapper.createTypeMap(BikeStation.class, BikeStationDTO.class)
                .addMappings(m -> m
                        .using(bikeStationBikesToBikeDTOsConverter)
                        .map(BikeStation::getBikes, BikeStationDTO::setBikes));

        mapper.createTypeMap(BikeStation.class, BikeStationPreviewDTO.class)
                .addMappings(m -> m
                        .when(Conditions.isNotNull())
                        .using(bikeStationFreeBikesConverter)
                        .map(x -> x, BikeStationPreviewDTO::setFreeBikes))
                .addMappings(m -> m
                        .when(Conditions.isNotNull())
                        .using(bikeStationFreeCapacityConverter)
                        .map(x -> x, BikeStationPreviewDTO::setFreeCapacity));

        // Bike-DTO specific converters, mappings
        mapper.createTypeMap(Bike.class, BikeDTO.class)
                .addMappings(m -> m
                        .using(bikeStationUUIDConverter)
                        .map(Bike::getCurStation, BikeDTO::setCurStationId));

        mapper.createTypeMap(CreateBikeDTO.class, Bike.class)
                .addMappings(m -> m
                        .when(Conditions.isNotNull())
                        .using(uuidBikeStationConverter)
                        .map(CreateBikeDTO::getCurrentBikeStationId, Bike::setCurStation));

        // Trip, TripDTO mapping
        mapper.createTypeMap(Trip.class, TripDTO.class)
                .addMappings(m -> m.map(src -> src.getBike().getId(), TripDTO::setBikeId))
                .addMappings(m -> m.map(src -> src.getUser().getId(), TripDTO::setUserId));
        // Booking-DTO specific converters, mappings
        mapper.createTypeMap(Booking.class, BookingDTO.class)
                .addMappings(m -> m
                        .using(bikeUUIDConverter)
                        .map(Booking::getBike, BookingDTO::setBookedBikeId))
                .addMappings(m -> m
                        .using(userUUIDConverter)
                        .map(Booking::getUser, BookingDTO::setUserId));

        mapper.createTypeMap(CreateBookingDTO.class, Booking.class)
                .addMappings(m -> m
                        .when(Conditions.isNotNull())
                        .using(uuidBikeConverter)
                        .map(CreateBookingDTO::getBookedBikeId, Booking::setBike));

        return mapper;
    }
}
