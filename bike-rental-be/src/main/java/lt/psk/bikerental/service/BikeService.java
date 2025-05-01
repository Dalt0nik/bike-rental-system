package lt.psk.bikerental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Bike.BikeDTO;
import lt.psk.bikerental.DTO.Bike.CreateBikeDTO;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.entity.BikeState;
import lt.psk.bikerental.entity.BikeStation;
import lt.psk.bikerental.repository.BikeRepository;
import lt.psk.bikerental.repository.BikeStationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BikeService {
    private final BikeRepository bikeRepository;
    private final BikeStationRepository bikeStationRepository;

    public List<BikeDTO> getAllBikes() {
        return bikeRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public BikeDTO getBikeById(UUID id) {
        return mapToDTO(bikeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bike not found")));
    }

    public BikeDTO createBike(CreateBikeDTO createBikeDTO) {
        Bike bike = new Bike();

        if (createBikeDTO.getCurrentBikeStationId() != null) {
            BikeStation station = bikeStationRepository.findById(createBikeDTO.getCurrentBikeStationId())
                    .orElseThrow(() -> new EntityNotFoundException("Station not found"));
            bike.setCurStation(station);
        }

        bike.setShortUniqueName(createBikeDTO.getShortUniqueName());
        bike.setState(createBikeDTO.getState());

        try {
            return mapToDTO(bikeRepository.save(bike));
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Short unique name already exists. Please retry.");
        }
    }

    public void deleteBike(UUID id) {
        if (!bikeRepository.existsById(id)) {
            throw new EntityNotFoundException("Bike not found");
        }
        bikeRepository.deleteById(id);
    }

    public BikeDTO updateBike(UUID id, CreateBikeDTO updateDTO) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bike not found"));

        if (updateDTO.getCurrentBikeStationId() != null) {
            BikeStation station = bikeStationRepository.findById(updateDTO.getCurrentBikeStationId())
                    .orElseThrow(() -> new EntityNotFoundException("Station not found"));
            bike.setCurStation(station);
        }

        bike.setShortUniqueName(updateDTO.getShortUniqueName());
        bike.setState(updateDTO.getState());
        return mapToDTO(bikeRepository.save(bike));
    }

    private BikeDTO mapToDTO(Bike bike) {
        return BikeDTO.builder()
                .id(bike.getId())
                .curStationId(bike.getCurStation() != null ? bike.getCurStation().getId() : null)
                .shortUniqueName(bike.getShortUniqueName())
                .state(bike.getState())
                .build();
    }
}
