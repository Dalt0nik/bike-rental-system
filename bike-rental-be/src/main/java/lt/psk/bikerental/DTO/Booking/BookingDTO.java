package lt.psk.bikerental.DTO.Booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private UUID id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID bookedBikeId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID bikeStationId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID userId;

    private Instant startTime;

    private Instant finishTime;
}
