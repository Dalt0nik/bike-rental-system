package lt.psk.bikerental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Check.CheckDTO;
import lt.psk.bikerental.config.BillingProperties;
import lt.psk.bikerental.entity.*;
import lt.psk.bikerental.repository.CheckRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckService {

    private final CheckRepository checkRepository;

    private final UserService userService;

    private final ModelMapper modelMapper;

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
            Instant finish = trip.getFinishTime();
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

    public CheckDTO getLatestCheck(Jwt jwt) {
        UUID userId = userService.getUserIdFromJwt(jwt);

        Check latestCheck = checkRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new EntityNotFoundException("No checks found for this user"));

        return modelMapper.map(latestCheck, CheckDTO.class);
    }

    public List<CheckDTO> getAllChecks(Jwt jwt) {
        UUID userId = userService.getUserIdFromJwt(jwt);

        return checkRepository.findAllByUserId(userId, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(check -> modelMapper.map(check, CheckDTO.class))
                .toList();
    }
}