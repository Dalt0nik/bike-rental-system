import { useWebSocket } from "./useWebSocket";

export interface StationUpdated {
  event: "station_updated";
  stationId: string;
  timestamp: string;
}

export function useMapWebSocket(
  onStationUpdated: (update: StationUpdated) => void
) {
  const {
    initializeWebSocketClient,
    subscribeToTopic,
    isConnected,
    deactivateConnection,
  } = useWebSocket();

  const init = () => {
    initializeWebSocketClient(
      () => {
        // onConnect
        console.log("Attempt to connect to map WebSocket");
        subscribeToTopic<StationUpdated>("/topic/map", (payload) => {
          if (payload.event === "station_updated") {
            onStationUpdated(payload);
          }
        });
      },
      () => {}
    );
  };

  return {
    init,
    isConnected,
    deactivateConnection,
  };
}
