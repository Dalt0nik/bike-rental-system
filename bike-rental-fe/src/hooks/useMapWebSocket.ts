import { useWebSocket } from "./useWebSocket";

export interface StationUpdated {
  event: "station_updated";
  stationId: string;
  timestamp: string;
}

export type WebSocketEvent = StationUpdated;

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
        subscribeToTopic<WebSocketEvent>("/topic/map", (payload) => {
          // eslint-disable-next-line "@typescript-eslint/no-unnecessary-condition"
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
