package lt.psk.bikerental.DTO.Check;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Trip.TripDTO;

@Data
public class ExpenseDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    BookingDTO bookingDTO;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    TripDTO tripDTO;

    CheckDTO checkDTO;
}
