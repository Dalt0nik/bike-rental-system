package lt.psk.bikerental.service;

import lombok.AllArgsConstructor;
import lt.psk.bikerental.DTO.BikeStation.BikeStationDTO;
import lt.psk.bikerental.DTO.BikeStation.CreateBikeStationDTO;
import lt.psk.bikerental.entity.BikeStation;
import lt.psk.bikerental.repository.BikeStationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class BikeStationService {

    private final BikeStationRepository bikeStationRepository;

    private final ModelMapper modelMapper;

    public List<BikeStationDTO> getAllBikeStations() {
        return bikeStationRepository.findAll().stream()
                .map(b -> modelMapper.map(b, BikeStationDTO.class))
                .toList();
    }

    public BikeStationDTO createBikeStation(CreateBikeStationDTO dto) {
        BikeStation entity = modelMapper.map(dto, BikeStation.class);
        BikeStation saved = bikeStationRepository.save(entity);
        return modelMapper.map(saved, BikeStationDTO.class);
    }
}
