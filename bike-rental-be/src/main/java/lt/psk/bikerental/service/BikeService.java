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
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BikeService {

    private final BikeRepository bikeRepository;

    private final BikeStationRepository bikeStationRepository;

    private final ModelMapper modelMapper;

    public List<BikeDTO> getAllBikes() {
        return bikeRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public BikeDTO getBikeById(UUID id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bike not found"));
        return mapToDTO(bike);
    }

    public List<BikeDTO> getAllBikesByStationId(UUID stationId) {
        return bikeRepository.findAllByCurStation_Id(stationId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public BikeDTO createBike(CreateBikeDTO createBikeDTO) {
        Bike bike = modelMapper.map(createBikeDTO, Bike.class);

        if (createBikeDTO.getCurrentBikeStationId() != null) {
            BikeStation station = bikeStationRepository.findById(createBikeDTO.getCurrentBikeStationId())
                    .orElseThrow(() -> new EntityNotFoundException("Station not found"));
            bike.setCurStation(station);
        }

        try {
            return mapToDTO(bikeRepository.save(bike));
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Short unique name already exists. Please retry.");
        }
    }

    @Transactional
    public List<BikeDTO> bulkCreateBikes(UUID stationId, int numberOfBikes) {
        BikeStation station = bikeStationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("Station not found"));

        List<Bike> bikes = new ArrayList<>();

        for (int i = 0; i < numberOfBikes; i++) {
            Bike bike = new Bike();
            bike.setCurStation(station);
            bike.setState(BikeState.FREE);
            bikes.add(bike);
        }

        List<Bike> saved = bikeRepository.saveAll(bikes);
        return saved.stream().map(this::mapToDTO).toList();
    }

    @Transactional
    public void deleteBike(UUID id) {
        if (!bikeRepository.existsById(id)) {
            throw new EntityNotFoundException("Bike not found");
        }
        bikeRepository.deleteById(id);
    }

    @Transactional
    public BikeDTO updateBike(UUID id, CreateBikeDTO updateDTO) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bike not found"));

        if (updateDTO.getCurrentBikeStationId() != null) {
            BikeStation station = bikeStationRepository.findById(updateDTO.getCurrentBikeStationId())
                    .orElseThrow(() -> new EntityNotFoundException("Station not found"));
            bike.setCurStation(station);
        }

        bike.setState(updateDTO.getState());
        return mapToDTO(bikeRepository.save(bike));
    }

    private BikeDTO mapToDTO(Bike bike) {
        BikeDTO dto = modelMapper.map(bike, BikeDTO.class);
        if (bike.getCurStation() != null) {
            dto.setCurStationId(bike.getCurStation().getId());
        }
        return dto;
    }
}
