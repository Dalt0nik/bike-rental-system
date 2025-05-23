package lt.psk.bikerental.repository;

import lt.psk.bikerental.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Optional<Booking> findFirstByUserIdAndIsActiveTrue(UUID userId);
    Optional<Booking> findFirstByBikeIdAndIsActiveTrueAndStartTimeBeforeAndFinishTimeAfter(
            UUID bikeId, Instant now1, Instant now2
    );

    Optional<Booking> findFirstByUserIdAndIsActiveTrueAndStartTimeBeforeAndFinishTimeAfter(
            UUID userId, Instant now1, Instant now2
    );

}
