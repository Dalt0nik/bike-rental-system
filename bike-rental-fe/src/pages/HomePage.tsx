import { useEffect, useRef, useState } from 'react';
import 'leaflet/dist/leaflet.css';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Map as LeafletMap } from 'leaflet';
import L from 'leaflet';

// Fix Leaflet marker icons
import iconUrl from 'leaflet/dist/images/marker-icon.png';
import iconShadow from 'leaflet/dist/images/marker-shadow.png';

const defaultIcon = L.icon({
    iconUrl,
    shadowUrl: iconShadow,
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41],
});
L.Marker.prototype.options.icon = defaultIcon;

// Define the data shape from backend
type BikeStation = {
    id: string;
    latitude: number;
    longitude: number;
    capacity: number;
    address: string;
};

function HomePage() {
    const mapRef = useRef<LeafletMap | null>(null);
    const [stations, setStations] = useState<BikeStation[]>([]);

    useEffect(() => {
        // Fetch stations from backend
        fetch('http://localhost:8080/bike-stations')
            .then(res => res.json())
            .then(data => setStations(data))
            .catch(err => console.error('Error fetching bike stations:', err));
    }, []);

    return (
        <div style={{
            position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
            display: 'flex', flexDirection: 'column'
        }}>
            <div style={{
                height: '60px',
                width: '100%',
                backgroundColor: '#3B82F6',
                color: 'white',
                display: 'flex',
                alignItems: 'center',
                paddingLeft: '16px',
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                zIndex: 1000
            }}>
                <div style={{ fontWeight: 'bold', fontSize: '20px'}}>Bike Rental Map</div>
            </div>

            <div style={{
                flex: 1, position: 'relative', width: '100vw',
                height: 'calc(100vh - 60px)'
            }}>
                <MapContainer
                    center={[54.68, 25.27]}
                    zoom={13}
                    style={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, width: '100%', height: '100%' }}
                    ref={mapRef}
                >
                    <TileLayer
                        attribution='&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a>'
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />

                    {/* Render markers */}
                    {stations.map(station => (
                        <Marker
                            key={station.id}
                            position={[station.latitude, station.longitude]}
                        >
                            <Popup>
                                <strong>{station.address}</strong><br />
                                Capacity: {station.capacity}
                            </Popup>
                        </Marker>
                    ))}
                </MapContainer>
            </div>
        </div>
    );
}

export default HomePage;
