package lt.psk.bikerental.entity.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import lt.psk.bikerental.entity.Bike;
import lt.psk.bikerental.service.ws.WsEventSendingService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BikeListener {
    private final WsEventSendingService wsEventSendingService;

    public BikeListener(@Lazy WsEventSendingService wsEventSendingService) {
        this.wsEventSendingService = wsEventSendingService;
    }

    @PrePersist
    @PreUpdate
    @PostPersist
    @PostUpdate
    private void onUpdatedBike(Bike bike) {
        if (bike.getCurStation() != null)
            wsEventSendingService.sendStationUpdated(bike.getCurStation());
    }
}
