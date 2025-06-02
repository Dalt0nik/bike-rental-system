package lt.psk.bikerental.repository;

import lt.psk.bikerental.entity.Check;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CheckRepository extends JpaRepository<Check, UUID> {
    List<Check> findAllByUserId(UUID userId, Sort sort);

    Optional<Check> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
}