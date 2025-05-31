package lt.psk.bikerental.DTO.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Trip.TripDTO;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStateDTO {
    private UUID id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BookingDTO booking;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TripDTO trip;
}
