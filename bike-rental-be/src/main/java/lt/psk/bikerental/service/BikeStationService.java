package lt.psk.bikerental.service;

import lombok.AllArgsConstructor;
import lt.psk.bikerental.DTO.BikeStation.BikeStationDTO;
import lt.psk.bikerental.DTO.BikeStation.CreateBikeStationDTO;
import lt.psk.bikerental.entity.BikeStation;
import lt.psk.bikerental.repository.BikeStationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class BikeStationService {

    private final BikeStationRepository bikeStationRepository;

    public List<BikeStationDTO> getAllBikeStations() {
        List<BikeStation> bikeStations = bikeStationRepository.findAll();
        return bikeStations.stream()
                .map(this::mapToBikeStationDTO)
                .toList();
    }

    public BikeStationDTO createBikeStation(CreateBikeStationDTO createBikeStationDTO) {
        BikeStation savedBikeStation =  bikeStationRepository.save(mapToBikeStation(createBikeStationDTO));
        return mapToBikeStationDTO(savedBikeStation);
    }


    private BikeStationDTO mapToBikeStationDTO (BikeStation bikeStation) {
        return BikeStationDTO.builder()
                .id(bikeStation.getId())
                .latitude(bikeStation.getLatitude())
                .longitude(bikeStation.getLongitude())
                .capacity(bikeStation.getCapacity())
                .address(bikeStation.getAddress())
                .build();
    }

    private BikeStation mapToBikeStation (CreateBikeStationDTO createBikeStationDTO) {
        BikeStation bikeStation = new BikeStation();
        bikeStation.setLatitude(createBikeStationDTO.getLatitude());
        bikeStation.setLongitude(createBikeStationDTO.getLongitude());
        bikeStation.setCapacity(createBikeStationDTO.getCapacity());

        if (createBikeStationDTO.getAddress() != null && !createBikeStationDTO.getAddress().trim().isEmpty()) {
            bikeStation.setAddress(createBikeStationDTO.getAddress());
        }
        
        return bikeStation;
    }
}
