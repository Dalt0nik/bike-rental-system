CREATE TABLE bikes
(
    id                UUID         NOT NULL,
    cur_station_id    UUID,
    state             VARCHAR(255) NOT NULL,
    short_unique_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_bikes PRIMARY KEY (id)
);

ALTER TABLE bikes
    ADD CONSTRAINT uc_bikes_short_unique_name UNIQUE (short_unique_name);

ALTER TABLE bikes
    ADD CONSTRAINT FK_BIKES_ON_CUR_STATION FOREIGN KEY (cur_station_id) REFERENCES bike_stations (id);