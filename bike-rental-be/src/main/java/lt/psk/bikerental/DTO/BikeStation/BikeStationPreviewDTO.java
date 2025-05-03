package lt.psk.bikerental.DTO.BikeStation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BikeStationPreviewDTO {
    private UUID id;

    private Double latitude;

    private Double longitude;

    private Integer capacity;

    private Integer count;

    private String address;
}
