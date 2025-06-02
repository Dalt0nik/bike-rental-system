package lt.psk.bikerental.DTO.Check;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class CheckDTO {
    private UUID id;
    private UUID userId;
    private UUID bookingId;
    private UUID tripId;
    private BigDecimal bookingFee;
    private BigDecimal unlockFee;
    private BigDecimal tripFee;
    private BigDecimal total;
    private Instant createdAt;
}