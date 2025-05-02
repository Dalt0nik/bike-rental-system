package lt.psk.bikerental.DTO.Bike;

import lombok.Builder;
import lombok.Data;
import lt.psk.bikerental.entity.BikeState;

import java.util.UUID;

@Data
@Builder
public class BikeDTO {
    private UUID id;
    private UUID curStationId;
    private BikeState state;
}
