import { useEffect, useRef } from 'react';
import 'leaflet/dist/leaflet.css';
import { MapContainer, TileLayer } from 'react-leaflet';
import { Map as LeafletMap } from 'leaflet';

function HomePage() {
    const mapRef = useRef<LeafletMap | null>(null);

    useEffect(() => {
        if (mapRef.current) {
            setTimeout(() => {
                mapRef.current?.invalidateSize();
            }, 100);
        }
    }, []);

    return (
        <div style={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            display: 'flex',
            flexDirection: 'column'
        }}>
            {/* Top Navigation Bar */}
            <div style={{
                height: '60px',
                width: '100%',
                backgroundColor: '#3B82F6',
                color: 'white',
                display: 'flex',
                alignItems: 'center',
                padding: '0 0px',
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                zIndex: 1000
            }}>
                <div style={{ fontWeight: 'bold', fontSize: '20px', padding: '16px'}}>Bike Rental Map</div>
                <div style={{ marginLeft: 'auto', display: 'flex', gap: '16px', paddingRight: '16px'}}>
                    <button style={{
                        background: 'none',
                        border: '1px solid white',
                        borderRadius: '4px',
                        padding: '6px 12px',
                        color: 'white',
                        cursor: 'pointer'
                    }}>
                        Search
                    </button>
                    <button style={{
                        background: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        padding: '6px 12px',
                        color: '#3B82F6',
                        cursor: 'pointer'
                    }}>
                        My Location
                    </button>
                </div>
            </div>

            {/* Map Container */}
            <div style={{
                flex: 1,
                position: 'relative',
                width: '100vw', /* Viewport width */
                height: 'calc(100vh - 60px)' /* Viewport height minus top bar */
            }}>
                <MapContainer
                    center={[54.68, 25.27]}
                    zoom={13}
                    style={{
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        width: '100%',
                        height: '100%'
                    }}
                    ref={mapRef}
                >
                    <TileLayer
                        attribution='&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a>'
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />
                </MapContainer>
            </div>
        </div>
    );
}

export default HomePage;