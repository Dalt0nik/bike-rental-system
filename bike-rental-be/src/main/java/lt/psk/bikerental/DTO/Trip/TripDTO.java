package lt.psk.bikerental.DTO.Trip;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TripDTO {
    private UUID id;
    private UUID bikeId;
    private UUID userId;
    private Instant startTime;
}
