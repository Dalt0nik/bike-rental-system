package lt.psk.bikerental.DTO.BikeStation;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class BikeStationDTO {
    private UUID id;

    private Double latitude;

    private Double longitude;

    private Integer capacity;

    private String address;
}
