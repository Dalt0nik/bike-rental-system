package lt.psk.bikerental.repository;

import lt.psk.bikerental.entity.BikeStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BikeStationRepository extends JpaRepository<BikeStation, UUID> {

}
