package lt.psk.bikerental.service;

import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.config.BillingProperties;
import lt.psk.bikerental.entity.*;
import lt.psk.bikerental.repository.CheckRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final CheckRepository checkRepository;
    private final BillingProperties billingProperties;

    public Check createAndSaveCheck(User user, Booking booking, Trip trip) {
        BigDecimal bookingFee = (booking != null)
                ? BigDecimal.valueOf(billingProperties.getBookingFee())
                : BigDecimal.ZERO;

        BigDecimal unlockFee = (trip != null && trip.getBooking() == null)
                ? BigDecimal.valueOf(billingProperties.getUnlockFee())
                : BigDecimal.ZERO;

        BigDecimal tripFee = BigDecimal.ZERO;
        if (trip != null) {
            Instant start = trip.getStartTime();
            Instant finish = trip.getFinishTime() != null ? trip.getFinishTime() : Instant.now();// i don't like this but for now it works
            long durationMinutes = Duration.between(start, finish).toMinutes();
            tripFee = BigDecimal.valueOf(billingProperties.getPricePerMinute())
                    .multiply(BigDecimal.valueOf(durationMinutes));
        }

        BigDecimal total = bookingFee.add(unlockFee).add(tripFee);

        Check check = new Check();
        check.setUser(user);
        check.setBooking(booking);
        check.setTrip(trip);
        check.setBookingFee(bookingFee);
        check.setUnlockFee(unlockFee);
        check.setTripFee(tripFee);
        check.setTotal(total);

        return checkRepository.save(check);
    }
}