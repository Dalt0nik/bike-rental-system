package lt.psk.bikerental.DTO.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Trip.TripDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStateDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BookingDTO booking;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TripDTO trip;
}
