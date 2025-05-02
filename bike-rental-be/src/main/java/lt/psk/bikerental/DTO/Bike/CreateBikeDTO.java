package lt.psk.bikerental.DTO.Bike;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lt.psk.bikerental.entity.BikeState;

import java.util.UUID;

@Data
public class CreateBikeDTO {

    private UUID currentBikeStationId;

    @NotNull(message = "State is required")
    private BikeState state;

}
