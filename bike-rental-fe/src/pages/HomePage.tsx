import { useRef} from 'react';
import 'leaflet/dist/leaflet.css';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Map as LeafletMap } from 'leaflet';
import L from 'leaflet';
import { useAuth0 } from '@auth0/auth0-react';
import { getAllBikeStations } from '../api/bikeStationApi';
import { useQuery } from "@tanstack/react-query";

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

function HomePage() {
    const mapRef = useRef<LeafletMap | null>(null);
    const { data: stations = [], isLoading, isError } = useQuery({
        queryKey: ['allBikeStations'],
        queryFn: getAllBikeStations
    });

    const { user, isAuthenticated, logout } = useAuth0();

    if (isLoading) return <div>Loading map...</div>;
    if (isError) return <div>Error loading stations.</div>;

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
                justifyContent: 'space-between',
                padding: '0 16px',
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                zIndex: 1000
            }}>
                <div style={{ fontWeight: 'bold', fontSize: '20px' }}>Bike Rental Map</div>

                {isAuthenticated && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginRight: '26px' }}>
                        <span>Welcome, {user?.email}</span>
                        <button
                            onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}
                            style={{
                                backgroundColor: 'white',
                                color: '#3B82F6',
                                border: 'none',
                                padding: '6px 30px',
                                borderRadius: '4px',
                                cursor: 'pointer',
                                fontWeight: 'bold'
                            }}
                        >
                            Log Out
                        </button>
                    </div>
                )}
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
                                Free Bikes: {station.freeBikes}<br />
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
