package lt.psk.bikerental.repository;

import lt.psk.bikerental.entity.Trip;
import lt.psk.bikerental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    Optional<Trip> findTopByUserAndFinishTimeIsNull(User user);
}