import { Marker, Popup } from "react-leaflet";
import { BikeStationPreviewResponse } from "../models/bikeStation";
import { useCreateBooking } from "../hooks/useCreateBooking";
import { getBikeStation } from "../api/bikeStationApi";
import L from "leaflet";
import { UserState, UserStatus } from "../models/user";
import { BikeState } from "../models/bike";

import iconUrl from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";
import { useStartTrip } from "../hooks/useStartTrip";
import { BookingResponse } from "../models/booking";

const defaultIcon = L.icon({
  iconUrl,
  shadowUrl: iconShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});

L.Marker.prototype.options.icon = defaultIcon;

// Orange icon for booked station or available stations during trip
const orangeIcon = L.icon({
  iconUrl: "data:image/svg+xml;base64," + btoa(`
    <svg width="25" height="41" viewBox="0 0 25 41" xmlns="http://www.w3.org/2000/svg">
      <path d="M12.5 0C5.6 0 0 5.6 0 12.5C0 19.4 12.5 41 12.5 41S25 19.4 25 12.5C25 5.6 19.4 0 12.5 0Z" fill="#ff8c00"/>
      <circle cx="12.5" cy="12.5" r="6" fill="white"/>
    </svg>
  `),
  shadowUrl: iconShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});

export interface BikeStationMarkerProps {
  station: BikeStationPreviewResponse;
  userState: UserState;
}

export function BikeStationMarker({ station, userState }: BikeStationMarkerProps) {
  const createBookingMutation = useCreateBooking();
  const startTripMutation = useStartTrip();

  const handleStartTrip = async (station: BikeStationPreviewResponse, bookingResponse?: BookingResponse) => {
    if (bookingResponse !== undefined) {
      startTripMutation.mutate({ bikeId: bookingResponse.bookedBikeId });
      return;
    }

    try {
      // Fetch the detailed station data to get actual bike IDs
      const stationDetails = await getBikeStation(station.id);

      // Find the first available bike (FREE state)
      const availableBike = stationDetails.bikes.find(bike => bike.state === BikeState.FREE);

      if (!availableBike) {
        console.error("No available bikes found at station", station.id);
        return;
      }

      startTripMutation.mutate({ bikeId: availableBike.id });
    } catch (error) {
      console.error("Failed to fetch station details:", error);
    }
  };

  const handleBookBike = async (station: BikeStationPreviewResponse) => {
    try {
      // Fetch the detailed station data to get actual bike IDs
      const stationDetails = await getBikeStation(station.id);

      // Find the first available bike (FREE state)
      const availableBike = stationDetails.bikes.find(bike => bike.state === BikeState.FREE);

      if (!availableBike) {
        console.error("No available bikes found at station", station.id);
        return;
      }

      // Book the actual bike ID
      createBookingMutation.mutate({
        bookedBikeId: availableBike.id,
        bikeStationId: station.id
      });
    } catch (error) {
      console.error("Failed to fetch station details:", error);
    }
  };

  const isUserBookingAtThisStation = userState.status === UserStatus.HAS_BOOKING && station.id === userState.booking.bikeStationId;

  return (
    <Marker
      position={[station.latitude, station.longitude]}
      icon={userState.status === UserStatus.ON_TRIP || isUserBookingAtThisStation ? orangeIcon : defaultIcon}
    >
      <Popup>
        <strong>{station.address}</strong>
        {userState.status === UserStatus.ON_TRIP && (
          <>
            <br /><span className="text-blue-600 font-bold">Available for Return</span>
            <br />Free Capacity: {station.freeCapacity}
          </>)}
        {isUserBookingAtThisStation && (
          <>
            <br /><span className="text-orange-600 font-bold">Booked bike in this station</span>
          </>)}
        {userState.status === UserStatus.FREE && (
          <>
            <br />Free Bikes: {station.freeBikes}
          </>)}
        {(station.freeBikes > 0 && userState.status === UserStatus.FREE || isUserBookingAtThisStation) && (
          <>
            <br />

            {userState.status === UserStatus.FREE && (
              <button
                onClick={() => void handleBookBike(station)}
                disabled={createBookingMutation.isPending}
                className="mt-2 bg-blue-500 hover:bg-blue-600 disabled:bg-gray-400 text-white px-3 py-1 rounded text-sm font-medium"
              >
                {createBookingMutation.isPending ? "Booking..." : "Book Bike"}
              </button>)}

            <button
              onClick={() => void handleStartTrip(station, isUserBookingAtThisStation ? userState.booking : undefined)}
              disabled={startTripMutation.isPending}
              className="mt-2 bg-blue-500 hover:bg-blue-600 disabled:bg-gray-400 text-white px-3 py-1 rounded text-sm font-medium"
            >
              {startTripMutation.isPending ? "Starting trip..." : "Start Trip"}
            </button>
          </>)}
        <br />Capacity: {station.capacity}
      </Popup>
    </Marker>
  );
}
