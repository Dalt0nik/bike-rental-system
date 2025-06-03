package lt.psk.bikerental.entity.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.repository.BikeRepository;
import lt.psk.bikerental.service.ws.WsEventSendingService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BikeListener {
    private final WsEventSendingService wsEventSendingService;
    private final BikeRepository bikeRepository;

    public BikeListener(
            @Lazy WsEventSendingService wsEventSendingService,
            @Lazy BikeRepository bikeRepository
    ) {
        this.wsEventSendingService = wsEventSendingService;
        this.bikeRepository = bikeRepository;
    }

    @PrePersist
    @PreUpdate
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void onUpdatingBike(Bike bike) {
        Bike oldBike = bikeRepository.findById(bike.getId()).orElse(null);
        if (oldBike.getCurStation() == null) {
            return;
        }
        oldBike.setState(bike.getState());
        wsEventSendingService.sendStationUpdated(oldBike.getCurStation());
    }

    @PostPersist
    @PostUpdate
    public void onUpdatedBike(Bike bike) {
        if (bike.getCurStation() != null) {
            wsEventSendingService.sendStationUpdated(bike.getCurStation());
        }
    }
}
