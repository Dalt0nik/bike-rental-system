package lt.psk.bikerental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "booked_bike_id", referencedColumnName = "id")
    private Bike bike;

    @Column(nullable = false)
    private Instant startTime = Instant.now();

    @Column(nullable = false)
    private Instant finishTime = startTime.plus(20, ChronoUnit.MINUTES);

    @Column(nullable = false)
    private boolean isActive = true;

    @Version
    @Column(nullable = false)
    private Long version;
}
