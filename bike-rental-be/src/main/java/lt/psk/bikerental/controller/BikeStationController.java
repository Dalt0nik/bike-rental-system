package lt.psk.bikerental.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lt.psk.bikerental.DTO.BikeStation.BikeStationDTO;
import lt.psk.bikerental.DTO.BikeStation.CreateBikeStationDTO;
import lt.psk.bikerental.service.BikeStationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/private/bike-stations")
@AllArgsConstructor
@RestController
public class BikeStationController {

    private final BikeStationService bikeStationService;

    @GetMapping
    public List<BikeStationDTO> getAllBikeStations() {
        return bikeStationService.getAllBikeStations();
    }

    @PostMapping
    public BikeStationDTO createBikeStation(@RequestBody @Valid CreateBikeStationDTO createBikeStationDTO) {
        return bikeStationService.createBikeStation(createBikeStationDTO);
    }
}
