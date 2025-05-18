package lt.psk.bikerental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bikes")
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

}
