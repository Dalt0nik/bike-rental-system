// src/hooks/useWebSocket.ts
import { useEffect, useRef, useState } from "react";
import {
  Client,
  IMessage,
  StompHeaders,
  StompSubscription,
} from "@stomp/stompjs";
import SockJS from "sockjs-client";

const WS_CONFIG = {
  url: "http://localhost:8080/ws",
  reconnectDelay: 2000,
};

export const useWebSocket = () => {
  const stompClientRef = useRef<Client | null>(null);
  const subscriptionsRef = useRef<Map<string, StompSubscription>>(new Map());
  const [isConnected, setIsConnected] = useState(false);
  const [messages, setMessages] = useState<IMessage[]>([]);

  useEffect(() => {
    return () => {
      // only deactivate if active, to avoid the StrictMode warning
      if (stompClientRef.current?.active) {
        stompClientRef.current.deactivate();
      }
    };
  }, []);

  const initializeWebSocketClient = (
    onConnect: () => void,
    onDisconnect: () => void,
  ) => {
    if (stompClientRef.current?.active) {
      console.warn(
        "WebSocket connection is already established. Call deactivateConnection() first to start a fresh one."
      );
      return;
    }

    const socket = new SockJS(WS_CONFIG.url);
    // no auth header by default
    const stompHeaders = new StompHeaders();

    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: WS_CONFIG.reconnectDelay,
      connectHeaders: stompHeaders,
      onConnect: () => {
        setIsConnected(true);
        onConnect();
        console.log("Connected to WebSocket");
      },
      onDisconnect: () => {
        setIsConnected(false);
        onDisconnect();
        console.log("Disconnected from WebSocket");
      },
      onWebSocketError: () => {
        setIsConnected(false);
        console.log("WebSocketError");
      },
      onStompError: () => {
        setIsConnected(false);
        console.log("StompError");
      },
      onWebSocketClose: () => {
        setIsConnected(false);
        console.log("WebSocketClose");
      },
    });

    stompClient.activate();
    stompClientRef.current = stompClient;
  };

  const deactivateConnection = () => {
    if (!stompClientRef.current?.active) {
      console.warn("Cannot deactivate on absent connection");
      return;
    }
    stompClientRef.current.deactivate();
    stompClientRef.current = null;
  };

  const subscribeToTopic = <EventResponse>(
    brokerPath: string,
    handleMessageCallback: (eventResponse: EventResponse) => void,
  ) => {
    if (!stompClientRef.current?.connected) return;

    subscriptionsRef.current.get(brokerPath)?.unsubscribe();

    const newSub = stompClientRef.current.subscribe(
      brokerPath,
      (message: IMessage) => {
        setMessages((prev) => [...prev, message]);
        handleMessageCallback(JSON.parse(message.body));
      },
    );
    subscriptionsRef.current.set(brokerPath, newSub);
  };

  const unsubscribeTopic = (brokerPath: string) => {
    stompClientRef.current?.connected &&
      subscriptionsRef.current.get(brokerPath)?.unsubscribe();
  };

  const sendMessage = (
    destinationPath: string,
    payload: Record<string, string>,
  ) => {
    if (!stompClientRef.current?.connected) return;
    stompClientRef.current.publish({
      destination: destinationPath,
      body: JSON.stringify(payload),
    });
  };

  return {
    initializeWebSocketClient,
    subscribeToTopic,
    unsubscribeTopic,
    sendMessage,
    isConnected,
    deactivateConnection,
    messages,
  };
};
