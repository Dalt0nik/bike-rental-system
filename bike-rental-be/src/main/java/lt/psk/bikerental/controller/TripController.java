package lt.psk.bikerental.controller;

import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Trip.CreateTripDTO;
import lt.psk.bikerental.DTO.Trip.TripDTO;
import lt.psk.bikerental.service.TripService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public TripDTO startTrip(@RequestBody CreateTripDTO dto, @AuthenticationPrincipal Jwt jwt) {
        return tripService.startTrip(dto, jwt);
    }
}
