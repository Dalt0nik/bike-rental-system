package lt.psk.bikerental.DTO.Bike;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lt.psk.bikerental.entity.BikeState;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BikeDTO {
    private UUID id;
    private UUID curStationId;
    private BikeState state;
}
