package lt.psk.bikerental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Bike.BikeDTO;
import lt.psk.bikerental.DTO.Bike.BulkCreateBikeDTO;
import lt.psk.bikerental.DTO.Bike.CreateBikeDTO;
import lt.psk.bikerental.service.BikeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bikes")
@RequiredArgsConstructor
public class BikeController {

    private final BikeService bikeService;

    @GetMapping
    public List<BikeDTO> getAllBikes() {
        return bikeService.getAllBikes();
    }

    @GetMapping("/{id}")
    public BikeDTO getBikeById(@PathVariable UUID id) {
        return bikeService.getBikeById(id);
    }

    @GetMapping("/station/{stationId}")
    public List<BikeDTO> getAllBikesByStationId(@PathVariable UUID stationId) {
        return bikeService.getAllBikesByStationId(stationId);
    }

    @PostMapping
    public BikeDTO createBike(@Valid @RequestBody CreateBikeDTO createBikeDTO) {
        return bikeService.createBike(createBikeDTO);
    }
    @PostMapping("/bulk")
    public List<BikeDTO> bulkCreateBikes(@Valid @RequestBody BulkCreateBikeDTO request) {
        return bikeService.bulkCreateBikes(request.getBikeStationId(), request.getNumberOfBikes());
    }

    @PutMapping("/{id}")
    public BikeDTO updateBike(@PathVariable UUID id, @Valid @RequestBody CreateBikeDTO bikeDTO) {
        return bikeService.updateBike(id, bikeDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteBike(@PathVariable UUID id) {
        bikeService.deleteBike(id);
    }
}
