package lt.psk.bikerental.entity.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import lt.psk.bikerental.entity.BikeStation;
import lt.psk.bikerental.service.ws.WsEventSendingService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BikeStationListener {
    private final WsEventSendingService wsEventSendingService;

    public BikeStationListener(@Lazy WsEventSendingService wsEventSendingService) {
        this.wsEventSendingService = wsEventSendingService;
    }

    @PostPersist
    @PostUpdate
    private void onUpdatedStation(BikeStation station) {
        wsEventSendingService.sendStationUpdated(station);
    }
}
