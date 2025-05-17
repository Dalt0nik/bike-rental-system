package lt.psk.bikerental.service.ws;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WsEventSendingService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendStationUpdated(UUID stationId) {
        messagingTemplate.convertAndSend(
                "/map",
                Map.of(
                        "event", "station_updated",
                        "stationId", stationId,
                        "timestamp", Instant.now().toString()
                )
        );
    }
}
