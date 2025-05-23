package lt.psk.bikerental.DTO.Trip;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateTripDTO {
    private UUID bikeId;
}