package lt.psk.bikerental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "checks")
public class Check {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(optional = true)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @OneToOne(optional = true)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal bookingFee = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unlockFee = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tripFee = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean paid = false;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
