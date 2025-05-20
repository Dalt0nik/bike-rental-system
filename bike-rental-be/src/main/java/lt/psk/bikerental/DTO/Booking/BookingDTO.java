package lt.psk.bikerental.DTO.Booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
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
    private UUID userId;

    private Timestamp startTime;

    private boolean isActive;

}
