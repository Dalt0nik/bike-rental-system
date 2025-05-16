package lt.psk.bikerental.DTO.Booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class CreateBookingDTO {
    @NotNull(message = "Bike ID is required")
    private UUID bookedBikeId;

    @NotNull(message = "User ID is required")
    private UUID userId;
}
