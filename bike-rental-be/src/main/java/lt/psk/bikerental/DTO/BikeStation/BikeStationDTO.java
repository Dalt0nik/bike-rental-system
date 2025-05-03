package lt.psk.bikerental.DTO.BikeStation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lt.psk.bikerental.DTO.Bike.BikeDTO;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BikeStationDTO {
    private UUID id;

    private Double latitude;

    private Double longitude;

    private Integer capacity;

    private List<BikeDTO> bikes;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String address;
}
