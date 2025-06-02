package lt.psk.bikerental.service;

import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Check.ExpenseDTO;
import lt.psk.bikerental.DTO.Check.CheckDTO;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ExpenseService {
    
    private final CheckService checkService;
    
    private final BookingService bookingService;
    
    private final TripService tripService;

    public List<ExpenseDTO> getAllExpenses(Jwt jwt) {
        List<ExpenseDTO> expenses = new ArrayList<>();

        List<CheckDTO> checks = checkService.getAllChecks(jwt);
        for (CheckDTO check : checks) {
            ExpenseDTO expense = new ExpenseDTO();
            expense.setCheckDTO(check);

            if(check.getBookingId() != null) {
                expense.setBookingDTO(bookingService.getBooking(check.getBookingId()));
            } else if(check.getTripId() != null) {
                expense.setTripDTO(tripService.getTrip(check.getTripId()));
            }

            expenses.add(expense);
        }

        return expenses;
    }
}
