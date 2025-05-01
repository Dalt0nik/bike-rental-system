package lt.psk.bikerental.repository;

import lt.psk.bikerental.entity.Bike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BikeRepository extends JpaRepository<Bike, UUID> {
    List<Bike> findAllByCurStation_Id(UUID stationId);
}
