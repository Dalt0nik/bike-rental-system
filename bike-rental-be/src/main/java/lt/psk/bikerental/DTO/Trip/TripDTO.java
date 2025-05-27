package lt.psk.bikerental.DTO.Trip;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TripDTO {
    private UUID id;
    private UUID bikeId;
    private UUID userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID bookingId;
    private Instant startTime;
}
