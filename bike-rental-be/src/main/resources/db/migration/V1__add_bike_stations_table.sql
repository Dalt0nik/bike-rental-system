CREATE TABLE bike_stations
(
    id        UUID             NOT NULL,
    latitude  DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    capacity  INTEGER          NOT NULL,
    address   VARCHAR(500),
    CONSTRAINT pk_bike_stations PRIMARY KEY (id)
);