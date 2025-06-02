package lt.psk.bikerental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lt.psk.bikerental.entity.listener.BikeStationListener;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bike_stations")
@EntityListeners(BikeStationListener.class)
public class BikeStation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Integer capacity;

    @Column(length = 500)
    private String address;

    @OneToMany(mappedBy = "curStation")
    private List<Bike> bikes;
}