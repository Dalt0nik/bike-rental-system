import { useRef, useEffect, useState } from "react"
import "leaflet/dist/leaflet.css"
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet"
import type { Map as LeafletMap } from "leaflet"
import L from "leaflet"
import { useQuery } from "@tanstack/react-query"
import { getAllBikeStations } from '../api/bikeStationApi';
import { getUserStatus } from '../api/userApi';
import Header from "../components/Header";
import { useMapWebSocket, StationUpdated } from "../hooks/useMapWebSocket";

// Fix Leaflet marker icons
import iconUrl from "leaflet/dist/images/marker-icon.png"
import iconShadow from "leaflet/dist/images/marker-shadow.png"

const defaultIcon = L.icon({
  iconUrl,
  shadowUrl: iconShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
})

// Orange icon for booked station or available stations during trip
const orangeIcon = L.icon({
  iconUrl: 'data:image/svg+xml;base64,' + btoa(`
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
})

L.Marker.prototype.options.icon = defaultIcon

function BookingTimer({ finishTime, stationAddress }: { finishTime: string; stationAddress?: string }) {
  const [timeLeft, setTimeLeft] = useState<string>("");

  useEffect(() => {
    const updateTimer = () => {
      const now = new Date().getTime();
      const finish = new Date(finishTime).getTime();
      const diff = finish - now;

      if (diff <= 0) {
        setTimeLeft("Expired");
        return;
      }

      const minutes = Math.floor(diff / (1000 * 60));
      const seconds = Math.floor((diff % (1000 * 60)) / 1000);
      setTimeLeft(`${minutes}:${seconds.toString().padStart(2, '0')}`);
    };

    updateTimer();
    const interval = setInterval(updateTimer, 1000);

    return () => clearInterval(interval);
  }, [finishTime]);

  return (
    <div className="absolute top-20 left-1/2 transform -translate-x-1/2 bg-orange-500 text-white px-4 py-2 rounded-lg shadow-lg z-[1000] max-w-[50%] text-center">
      <div className="font-bold">Booking expires in:</div>
      <div className="text-xl font-mono">{timeLeft}</div>
      {stationAddress && (
        <div className="text-sm mt-1">Station: {stationAddress}</div>
      )}
    </div>
  );
}

function TripTimer({ startTime }: { startTime: string }) {
  const [timeElapsed, setTimeElapsed] = useState<string>("");

  useEffect(() => {
    const updateTimer = () => {
      const now = new Date().getTime();
      const start = new Date(startTime).getTime();
      const diff = now - start;

      const hours = Math.floor(diff / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((diff % (1000 * 60)) / 1000);

      if (hours > 0) {
        setTimeElapsed(`${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`);
      } else {
        setTimeElapsed(`${minutes}:${seconds.toString().padStart(2, '0')}`);
      }
    };

    updateTimer();
    const interval = setInterval(updateTimer, 1000);

    return () => clearInterval(interval);
  }, [startTime]);

  return (
    <div className="absolute top-20 left-1/2 transform -translate-x-1/2 bg-blue-500 text-white px-4 py-2 rounded-lg shadow-lg z-[1000] max-w-[50%] text-center">
      <div className="font-bold">Time traveled:</div>
      <div className="text-xl font-mono">{timeElapsed}</div>
      <div className="text-sm mt-1">🚴 Trip in progress</div>
    </div>
  );
}

export default function HomePage() {
  const mapRef = useRef<LeafletMap | null>(null)
  const { data: stations = [], isLoading: isBikesLoading, isError: isBikesError } = useQuery({
    queryKey: ['allBikeStations'],
    queryFn: getAllBikeStations
  })

  const { data: userStatus, isLoading: isStatusLoading, isError: isStatusError } = useQuery({
    queryKey: ['userStatus'],
    queryFn: getUserStatus
  });

  const { init, deactivateConnection } = useMapWebSocket((update: StationUpdated) => {
    console.log("Station updated:", update);
  });

  useEffect(() => {
    init();

    return () => {
      deactivateConnection();
    };
  }, []);

  if (isBikesLoading || isStatusLoading)
    return (
      <div className="flex items-center justify-center h-screen">
        Loading map…
      </div>
    )
  if (isBikesError)
    return (
      <div className="flex items-center justify-center h-screen">
        Error loading stations.
      </div>
    )
  if (isStatusError)
    return (
      <div className="flex items-center justify-center h-screen">
        Error loading user status.
      </div>
    )

  const hasActiveBooking = userStatus?.booking?.active;
  const bookedStationId = userStatus?.booking?.bikeStationId;
  const hasActiveTrip = userStatus?.trip && !userStatus.trip.finishTime;
  
  const isBookingNotExpired = userStatus?.booking?.finishTime 
    ? new Date(userStatus.booking.finishTime).getTime() > new Date().getTime()
    : false;

  const hasConflict = hasActiveBooking && hasActiveTrip;

  if (hasConflict) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <div className="text-red-500 text-xl font-bold mb-2">Server Error</div>
          <div>Cannot have both active booking and trip simultaneously</div>
        </div>
      </div>
    );
  }

  // Determine which stations to render based on user state
  let stationsToRender = stations;
  let useOrangeIcon = false;

  if (hasActiveTrip) {
    // During trip: show only stations with free capacity > 0
    stationsToRender = stations.filter(station => station.freeCapacity > 0);
    useOrangeIcon = true;
  } else if (hasActiveBooking && bookedStationId) {
    // During booking: show only the booked station
    stationsToRender = stations.filter(station => station.id === bookedStationId);
    useOrangeIcon = true;
  }

  const bookedStation = hasActiveBooking && bookedStationId
    ? stations.find(station => station.id === bookedStationId)
    : null;

  return (
    <div className="fixed inset-0 flex flex-col">
      <Header />

      {/* Booking timer */}
      {hasActiveBooking && userStatus?.booking?.finishTime && isBookingNotExpired && !hasActiveTrip && (
        <BookingTimer 
          finishTime={userStatus.booking.finishTime} 
          stationAddress={bookedStation?.address}
        />
      )}

      {/* Trip timer */}
      {hasActiveTrip && userStatus?.trip?.startTime && (
        <TripTimer startTime={userStatus.trip.startTime} />
      )}

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

          {stationsToRender.map((station) => (
            <Marker
              key={station.id}
              position={[station.latitude, station.longitude]}
              icon={useOrangeIcon ? orangeIcon : defaultIcon}
            >
              <Popup>
                <strong>{station.address}</strong>
                <br />
                {hasActiveTrip ? (
                  <>
                    <span className="text-blue-600 font-bold">Available for Return</span>
                    <br />
                    Free Capacity: {station.freeCapacity}
                    <br />
                    Capacity: {station.capacity}
                    <br />
                  </>
                ) : hasActiveBooking && station.id === bookedStationId ? (
                  <>
                    <span className="text-orange-600 font-bold">Booked bike in this station</span>
                  </>
                ) : (
                  <>
                    Free Bikes: {station.freeBikes}
                    <br />
                    Capacity: {station.capacity}
                    <br />
                  </>
                )}
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>
    </div>
  )
}
