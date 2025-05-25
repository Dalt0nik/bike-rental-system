package lt.psk.bikerental.DTO.User;

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
public class UserStatusDTO {
    private boolean hasActiveBooking;
    private boolean hasOngoingTrip;
    private boolean free;
    private BookingDTO booking;
    private TripDTO trip;
}
