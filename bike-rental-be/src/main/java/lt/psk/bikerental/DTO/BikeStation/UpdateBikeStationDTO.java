package lt.psk.bikerental.DTO.BikeStation;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateBikeStationDTO {
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String address;
}
