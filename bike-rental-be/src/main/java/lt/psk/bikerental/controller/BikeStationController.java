package lt.psk.bikerental.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lt.psk.bikerental.DTO.BikeStation.BikeStationDTO;
import lt.psk.bikerental.DTO.BikeStation.BikeStationPreviewDTO;
import lt.psk.bikerental.DTO.BikeStation.CreateBikeStationDTO;
import lt.psk.bikerental.DTO.BikeStation.UpdateBikeStationDTO;
import lt.psk.bikerental.service.BikeStationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/bike-stations")
@AllArgsConstructor
@RestController
public class BikeStationController {

    private final BikeStationService bikeStationService;

    @GetMapping
    public List<BikeStationPreviewDTO> getAllBikeStations() {
        return bikeStationService.getAllBikeStations();
    }

    @GetMapping("/{id}")
    public BikeStationDTO getBikeStation(@PathVariable UUID id) {
        return bikeStationService.getBikeStation(id);
    }

    @PostMapping
    public BikeStationDTO createBikeStation(@RequestBody @Valid CreateBikeStationDTO createBikeStationDTO) {
        return bikeStationService.createBikeStation(createBikeStationDTO);
    }

    @PutMapping("/{id}")
    public BikeStationDTO updateBikeStation(@PathVariable UUID id, @RequestBody @Valid UpdateBikeStationDTO updateBikeStationDTO) {
        return bikeStationService.updateBikeStation(id, updateBikeStationDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteBikeStation(@PathVariable UUID id) {
        bikeStationService.deleteBikeStation(id);
    }

    @PostMapping("{stationId}/park/{bikeId}")
    public void parkBike(@PathVariable UUID stationId, @PathVariable UUID bikeId) {
        bikeStationService.parkBike(stationId, bikeId);
    }
}
