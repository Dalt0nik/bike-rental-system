import { useCallback, useEffect, useRef } from "react";
import "leaflet/dist/leaflet.css";
import { MapContainer, TileLayer } from "react-leaflet";
import type { Map as LeafletMap } from "leaflet";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { getAllBikeStations } from "../api/bikeStationApi";
import { getUserState } from "../api/userApi";
import Header from "../components/Header";
import { useMapWebSocket } from "../hooks/useMapWebSocket";

// Fix Leaflet marker icons
import { BikeStationMarker } from "../components/BikeStationMarker";
import { UserStatus, deriveUserState } from "../models/user";
import { BookingTimer } from "../components/BookingTimer";
import { TripTimer } from "../components/TripTimer";
import { BikeStationPreviewResponse } from "../models/bikeStation";

export default function HomePage() {
  const queryClient = useQueryClient();

  const mapRef = useRef<LeafletMap | null>(null);
  const { data: stations = [], isLoading: isBikesLoading, isError: isBikesError } = useQuery({
    queryKey: ["allBikeStations"],
    queryFn: getAllBikeStations
  });

  const { data: userStateResponse, isLoading: isStatusLoading, isError: isStatusError } = useQuery({
    queryKey: ["userState"],
    queryFn: getUserState
  });

  const onStationUpdated = useCallback((station: BikeStationPreviewResponse) => {
    queryClient.setQueryData(["allBikeStations"], (old: BikeStationPreviewResponse[]) => old.filter(x => x.id !== station.id).concat(station));
    console.log("Station updated:", station);
  }, [queryClient]);

  const { init, isConnected, deactivateConnection } = useMapWebSocket(onStationUpdated);

  useEffect(() => {
    if (!isConnected)
      init();

    return () => {
      if (isConnected)
        deactivateConnection();
    };
  }, [init, isConnected, deactivateConnection]);

  if (isBikesLoading || isStatusLoading)
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  if (isBikesError)
    return (
      <div className="flex items-center justify-center h-screen">
        Error loading stations.
      </div>
    );
  if (isStatusError || userStateResponse === undefined)
    return (
      <div className="flex items-center justify-center h-screen">
        Error loading user status.
      </div>
    );

  const userState = deriveUserState(userStateResponse);
  if (userState.status === UserStatus.INVALID) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <div className="text-red-500 text-xl font-bold mb-2">Server Error</div>
          <div>Cannot have both active booking and trip simultaneously.</div>
          <div>Contact support with your user id: {userStateResponse.id}.</div>
        </div>
      </div>
    );
  }

  // Determine which stations to render based on user state
  let stationsToRender = stations;
  if (userState.status === UserStatus.ON_TRIP) {
    // During trip: show only stations with free capacity > 0
    stationsToRender = stations.filter(station => station.freeCapacity > 0);
  } else if (userState.status === UserStatus.HAS_BOOKING) {
    // During booking: show only the booked station
    stationsToRender = stations.filter(station => station.id === userState.booking.bikeStationId);
  }

  return (
    <div className="fixed inset-0 flex flex-col">
      <Header />
      {/* Booking timer */}
      {userState.status === UserStatus.HAS_BOOKING &&
        <BookingTimer
          bookingId={userState.booking.id}
          finishTime={userState.booking.finishTime}
          stationAddress={stations.find(station => station.id === userState.booking.bikeStationId)?.address}
        />
      }

      {/* Trip timer */}
      {userState.status === UserStatus.ON_TRIP &&
        <TripTimer
          tripId={userState.trip.id}
          startTime={userState.trip.startTime}
        />
      }

      <div className="relative flex-1 w-full">
        <MapContainer
          center={[54.68, 25.27]}
          zoom={13}
          className="absolute inset-0 w-full h-full"
          ref={mapRef}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          {stationsToRender.map((station) =>
            <BikeStationMarker
              key={station.id}
              station={station}
              userState={userState}
            />)}
        </MapContainer>
      </div>
    </div>
  );
}
