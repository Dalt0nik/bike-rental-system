package lt.psk.bikerental.service.ws;

import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.BikeStation.BikeStationPreviewDTO;
import lt.psk.bikerental.entity.BikeStation;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WsEventSendingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ModelMapper modelMapper;

    public void sendStationUpdated(BikeStation station) {
        messagingTemplate.convertAndSend(
                "/topic/map",
                Map.of(
                        "event", "station_updated",
                        "timestamp", Instant.now().toString(),
                        "station", modelMapper.map(station, BikeStationPreviewDTO.class)
                )
        );
    }
}
