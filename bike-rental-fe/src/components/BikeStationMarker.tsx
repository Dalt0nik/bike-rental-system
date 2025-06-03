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

const createCustomIcon = (color: string) => L.icon({
  iconUrl: "data:image/svg+xml;base64," + btoa(`
    <svg width="25" height="41" viewBox="0 0 25 41" xmlns="http://www.w3.org/2000/svg">
      <path d="M12.5 0C5.6 0 0 5.6 0 12.5C0 19.4 12.5 41 12.5 41S25 19.4 25 12.5C25 5.6 19.4 0 12.5 0Z" fill="${color}"/>
      <circle cx="12.5" cy="12.5" r="6" fill="white"/>
    </svg>
  `),
  shadowUrl: iconShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});

// Orange icon for booked station or available stations during trip
const defaultWhiteIcon = createCustomIcon("#808ceb");
const orangeIcon = createCustomIcon("#ff8c00");
const darkBlueIcon = createCustomIcon("#7b2cbf");
const grayIcon = createCustomIcon("#eaeaea");
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

  const getMarkerIcon = () => {
    if (userState.status === UserStatus.ON_TRIP) {
      return darkBlueIcon;  // dark blue for trip
    }
    if (isUserBookingAtThisStation) {
      return orangeIcon;  // Orange for booking
    }
    if (station.freeBikes === 0) {
      return grayIcon; // gray icon if no free bikes
    }
    return defaultWhiteIcon;  // Default white for free stations
  };


  const getTripPricing = () => {
    if (userState.status === UserStatus.FREE) {
      return "€1.00 + €0.50/min";
    } else if (userState.status === UserStatus.HAS_BOOKING) {
      return "€0.50/min";
    }
    return "";
  };

  return (
    <Marker
      position={[station.latitude, station.longitude]}
      icon={getMarkerIcon()}
    >
      <Popup>
        <strong>{station.address}</strong>
        {userState.status === UserStatus.ON_TRIP && (
          <>
            <br /><span className="text-blue-darker font-bold">Available for Return</span>
            <br />Free parking spaces: {station.freeCapacity}
          </>)}
        {isUserBookingAtThisStation && (
          <>
            <br /><span className="text-orange-dark font-bold">Booked bike in this station</span>
          </>)}
        {userState.status === UserStatus.FREE && (
          <>
            <br />Free Bikes: {station.freeBikes}
          </>)}
        
        {(station.freeBikes > 0 && userState.status === UserStatus.FREE || isUserBookingAtThisStation) && (
          <div className="mt-2 space-y-2">
            {userState.status === UserStatus.FREE && (
              <div>
                <div className="text-xs text-gray-600 mb-1">Booking fee: €2.00</div>
                <button
                  onClick={() => void handleBookBike(station)}
                  disabled={createBookingMutation.isPending}
                  className="w-full bg-blue-main hover:bg-blue-darker disabled:bg-gray-400 text-white px-3 py-1 rounded text-sm font-medium block"
                >
                  {createBookingMutation.isPending ? "Booking..." : "Book Bike"}
                </button>
              </div>
            )}

            <div>
              <div className="text-xs text-gray-600 mb-1">Trip cost: {getTripPricing()}</div>
              <button
                onClick={() => void handleStartTrip(station, isUserBookingAtThisStation ? userState.booking : undefined)}
                disabled={startTripMutation.isPending}
                className="w-full bg-blue-main hover:bg-blue-darker disabled:bg-gray-400 text-white px-3 py-1 rounded text-sm font-medium block"
              >
                {startTripMutation.isPending ? "Starting trip..." : "Start Trip"}
              </button>
            </div>
          </div>
        )}
        
        <br />Capacity: {station.capacity}
      </Popup>
    </Marker>
  );
}