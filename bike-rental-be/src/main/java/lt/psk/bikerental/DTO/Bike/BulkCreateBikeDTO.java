package lt.psk.bikerental.DTO.Bike;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class BulkCreateBikeDTO {

    @NotNull(message = "Station ID is required")
    private UUID bikeStationId;

    @Min(value = 1, message = "At least one bike must be created")
    private int numberOfBikes;
}
