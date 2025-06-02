package lt.psk.bikerental.config;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Bike.BikeDTO;
import lt.psk.bikerental.DTO.Bike.CreateBikeDTO;
import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Booking.CreateBookingDTO;
import lt.psk.bikerental.DTO.Check.CheckDTO;
import lt.psk.bikerental.DTO.Trip.TripDTO;
import lt.psk.bikerental.entity.*;
import lt.psk.bikerental.repository.BikeRepository;
import lt.psk.bikerental.repository.BikeStationRepository;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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
                .addMapping(Booking::getUser, BookingDTO::setUserId)
                .addMapping(b -> b.getBike().getCurStation(), BookingDTO::setBikeStationId);

        mapper.createTypeMap(CreateBookingDTO.class, Booking.class)
                .addMapping(CreateBookingDTO::getBookedBikeId, Booking::setBike);

        mapper.addMappings(new PropertyMap<Check, CheckDTO>() {
            @Override
            protected void configure() {
                map().setUserId(source.getUser().getId());
                map().setBookingId(null); // explicitly set to null by default
                map().setTripId(null);
            }
        });

        // Booking converter
        Converter<Check, UUID> bookingIdConverter = ctx -> {
            Check src = ctx.getSource();
            return (src.getBooking() != null) ? src.getBooking().getId() : null;
        };
        mapper.typeMap(Check.class, CheckDTO.class)
                .addMappings(m -> m.using(bookingIdConverter).map(src -> src, CheckDTO::setBookingId));

        // Trip converter
        Converter<Check, UUID> tripIdConverter = ctx -> {
            Check src = ctx.getSource();
            return (src.getTrip() != null) ? src.getTrip().getId() : null;
        };
        mapper.typeMap(Check.class, CheckDTO.class)
                .addMappings(m -> m.using(tripIdConverter).map(src -> src, CheckDTO::setTripId));
        return mapper;
    }
}
