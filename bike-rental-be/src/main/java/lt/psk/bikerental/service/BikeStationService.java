package lt.psk.bikerental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lt.psk.bikerental.DTO.BikeStation.BikeStationDTO;
import lt.psk.bikerental.DTO.BikeStation.BikeStationPreviewDTO;
import lt.psk.bikerental.DTO.BikeStation.CreateBikeStationDTO;
import lt.psk.bikerental.DTO.BikeStation.UpdateBikeStationDTO;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.entity.BikeStation;
import lt.psk.bikerental.repository.BikeRepository;
import lt.psk.bikerental.repository.BikeStationRepository;
import lt.psk.bikerental.service.ws.WsEventSendingService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class BikeStationService {

    private final BikeStationRepository bikeStationRepository;

    private final ModelMapper modelMapper;

    private final BikeRepository bikeRepository;
    private final WsEventSendingService wsEventSendingService;

    public List<BikeStationPreviewDTO> getAllBikeStations() {
        return bikeStationRepository.findAll().stream()
                .map(x -> modelMapper.map(x, BikeStationPreviewDTO.class))
                .toList();
    }

    public BikeStationDTO getBikeStation(UUID id) {
        return bikeStationRepository.findById(id)
                .map(x -> modelMapper.map(x, BikeStationDTO.class))
                .orElseThrow(() -> new EntityNotFoundException("Bike station not found"));
    }

    @Transactional
    public BikeStationDTO createBikeStation(CreateBikeStationDTO dto) {
        BikeStation entity = modelMapper.map(dto, BikeStation.class);
        entity.setBikes(new ArrayList<>());
        BikeStation saved = bikeStationRepository.save(entity);
        wsEventSendingService.sendStationUpdated(saved);

        return modelMapper.map(saved, BikeStationDTO.class);
    }

    @Transactional
    public BikeStationDTO updateBikeStation(UUID id, UpdateBikeStationDTO dto) {
        BikeStation station = bikeStationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bike station not found"));

        modelMapper.map(dto, station);
        if (station.getCapacity() < station.getBikes().size())
            throw new RuntimeException("Bike station cannot have less capacity than current bikes");

        BikeStation saved = bikeStationRepository.save(station);
        wsEventSendingService.sendStationUpdated(saved);

        return modelMapper.map(saved, BikeStationDTO.class);
    }

    @Transactional
    public void deleteBikeStation(UUID id) {
        if (!bikeStationRepository.existsById(id))
            throw new EntityNotFoundException("Bike station not found");

        bikeStationRepository.deleteById(id);
    }

    @Transactional
    public void parkBike(UUID bikeStationId, UUID bikeId) {
        Bike bike = bikeRepository.findById(bikeId)
                .orElseThrow(() -> new EntityNotFoundException("Bike not found"));

        BikeStation bikeStation = bikeStationRepository.findById(bikeStationId)
                .orElseThrow(() -> new EntityNotFoundException("Bike station not found"));

        bike.setCurStation(bikeStation);
        bikeRepository.save(bike);
        bikeStation.getBikes().add(bike);
        wsEventSendingService.sendStationUpdated(bikeStation);
    }
}
