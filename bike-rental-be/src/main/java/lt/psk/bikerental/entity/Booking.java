package lt.psk.bikerental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
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
    private User user; // maybe just ID is better?

    @ManyToOne
    @JoinColumn(name = "booked_bike_id", referencedColumnName = "id")
    private Bike bike; // maybe just ID is better?

    @Column(nullable = false)
    private Timestamp startTime;

    @Column(nullable = false)
    private boolean isActive = true;
}
