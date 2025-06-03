package lt.psk.bikerental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lt.psk.bikerental.entity.listener.BikeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bikes")
@EntityListeners(BikeListener.class)
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cur_station_id", referencedColumnName = "id")
    private BikeStation curStation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BikeState state = BikeState.FREE;

    @OneToMany(mappedBy = "bike")
    private List<Booking> bookings = new ArrayList<>();


}
