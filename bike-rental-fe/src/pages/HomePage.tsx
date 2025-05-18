import { useRef, useEffect } from "react"
import "leaflet/dist/leaflet.css"
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet"
import type { Map as LeafletMap } from "leaflet"
import L from "leaflet"
import { useQuery } from "@tanstack/react-query"
import { getAllBikeStations } from '../api/bikeStationApi';
import Header from "../components/Header"
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
L.Marker.prototype.options.icon = defaultIcon

export default function HomePage() {
  const mapRef = useRef<LeafletMap | null>(null)
  const { data: stations = [], isLoading, isError } = useQuery({
    queryKey: ['allBikeStations'],
    queryFn: getAllBikeStations
  })

  const { init, deactivateConnection } = useMapWebSocket((update: StationUpdated) => {
    console.log("Station updated:", update);
  });

  useEffect(() => {
    init();

    return () => {
      deactivateConnection();
    };
  }, []);


  if (isLoading)
    return (
      <div className="flex items-center justify-center h-screen">
        Loading map…
      </div>
    )
  if (isError)
    return (
      <div className="flex items-center justify-center h-screen">
        Error loading stations.
      </div>
    )

  return (
    
    <div className="fixed inset-0 flex flex-col">
      <Header />

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

          {stations.map((station) => (
            <Marker
              key={station.id}
              position={[station.latitude, station.longitude]}
            >
              <Popup>
                <strong>{station.address}</strong>
                <br />
                Free Bikes: {station.freeBikes}
                <br />
                Capacity: {station.capacity}
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>
    </div>
  )
}
