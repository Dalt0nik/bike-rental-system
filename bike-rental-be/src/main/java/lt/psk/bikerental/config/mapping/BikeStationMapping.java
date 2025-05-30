package lt.psk.bikerental.config.mapping;

import jakarta.persistence.EntityNotFoundException;
import lt.psk.bikerental.DTO.Bike.BikeDTO;
import lt.psk.bikerental.DTO.BikeStation.BikeStationDTO;
import lt.psk.bikerental.DTO.BikeStation.BikeStationPreviewDTO;
import lt.psk.bikerental.DTO.BikeStation.UpdateBikeStationDTO;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.entity.BikeState;
import lt.psk.bikerental.entity.BikeStation;
import lt.psk.bikerental.repository.BikeStationRepository;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
public class BikeStationMapping {
    public BikeStationMapping(ModelMapper mapper, BikeStationRepository bikeStationRepository) {
        mapper.addConverter(v -> v.getSource() != null ? v.getSource().getId() : null, BikeStation.class, UUID.class);
        mapper.addConverter(v -> bikeStationRepository.findById(v.getSource())
                        .orElseThrow(() -> new EntityNotFoundException("Station not found")),
                UUID.class,
                BikeStation.class);

        Converter<BikeStation, Integer> bikeStationFreeBikesConverter = v ->
                v.getSource() != null
                        ? (int)v.getSource().getBikes().stream().filter(x -> x.getState() == BikeState.FREE).count()
                        : 0;

        Converter<BikeStation, Integer> bikeStationFreeCapacityConverter = v ->
                v.getSource() != null
                        ? v.getSource().getCapacity() - v.getSource().getBikes().size()
                        : 0;

        Converter<List<Bike>, List<BikeDTO>> bikeStationBikesToBikeDTOsConverter = v ->
                v.getSource() != null
                        ? v.getSource().stream().map(x -> mapper.map(x, BikeDTO.class)).toList()
                        : new ArrayList<>();

        mapper.createTypeMap(BikeStation.class, BikeStationDTO.class)
                .addMappings(m -> m
                        .using(bikeStationBikesToBikeDTOsConverter)
                        .map(BikeStation::getBikes, BikeStationDTO::setBikes));

        mapper.createTypeMap(BikeStation.class, BikeStationPreviewDTO.class)
                .addMappings(m -> m
                        .using(bikeStationFreeBikesConverter)
                        .map(x -> x, BikeStationPreviewDTO::setFreeBikes))
                .addMappings(m -> m
                        .using(bikeStationFreeCapacityConverter)
                        .map(x -> x, BikeStationPreviewDTO::setFreeCapacity));

        mapper.createTypeMap(UpdateBikeStationDTO.class, BikeStation.class)
                .addMappings(m -> m
                        .when(Conditions.isNotNull())
                        .map(UpdateBikeStationDTO::getCapacity, BikeStation::setCapacity));
    }
}
