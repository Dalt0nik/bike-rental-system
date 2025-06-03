import { useCallback } from "react";
import { BikeStationPreviewResponse } from "../models/bikeStation";
import { useWebSocket } from "./useWebSocket";

interface StationUpdated {
  event: "station_updated";
  station: BikeStationPreviewResponse;
}

type WebSocketEvent = StationUpdated;

export function useMapWebSocket(
  onStationUpdated: (station: BikeStationPreviewResponse) => void
) {
  const {
    initializeWebSocketClient,
    subscribeToTopic,
    isConnected,
    deactivateConnection,
  } = useWebSocket();

  const init = useCallback(() => {
    initializeWebSocketClient(
      () => {
        // onConnect
        console.log("Attempt to connect to map WebSocket");
        subscribeToTopic<WebSocketEvent>("/topic/map", (payload) => {
          switch (payload.event) {
          // eslint-disable-next-line "@typescript-eslint/no-unnecessary-condition"
          case ("station_updated"):
            onStationUpdated(payload.station);
            break;
          }
        });
      },
      () => {}
    );
  }, [initializeWebSocketClient, onStationUpdated, subscribeToTopic]);

  return {
    init,
    isConnected,
    deactivateConnection,
  };
}
