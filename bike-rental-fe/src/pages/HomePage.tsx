import { useEffect, useRef } from "react";
import "leaflet/dist/leaflet.css";
import { MapContainer, TileLayer } from "react-leaflet";
import type { Map as LeafletMap } from "leaflet";
import { useQuery } from "@tanstack/react-query";
import { getAllBikeStations } from "../api/bikeStationApi";
import { getUserState } from "../api/userApi";
import Header from "../components/Header";
import { StationUpdated, useMapWebSocket } from "../hooks/useMapWebSocket";

// Fix Leaflet marker icons
import { BikeStationMarker } from "../components/BikeStationMarker";
import { UserStatus, deriveUserState } from "../models/user";
import { BookingTimer } from "../components/BookingTimer";
import { TripTimer } from "../components/TripTimer";

export default function HomePage() {
  const mapRef = useRef<LeafletMap | null>(null);
  const { data: stations = [], isLoading: isBikesLoading, isError: isBikesError } = useQuery({
    queryKey: ["allBikeStations"],
    queryFn: getAllBikeStations
  });

  const { data: userStateResponse, isLoading: isStatusLoading, isError: isStatusError } = useQuery({
    queryKey: ["userState"],
    queryFn: getUserState
  });

  const { init, isConnected, deactivateConnection } = useMapWebSocket((update: StationUpdated) => {
    console.log("Station updated:", update);
  });

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
        Loading map...
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
          <div>Contact support with your user ID: {userStateResponse.id}.</div>
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
          finishTime={userState.booking.finishTime}
          stationAddress={stations.find(station => station.id === userState.booking.bikeStationId)?.address}
        />
      }

      {/* Trip timer */}
      {userState.status === UserStatus.ON_TRIP &&
        <TripTimer startTime={userState.trip.startTime} />
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
