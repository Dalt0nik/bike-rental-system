CREATE TABLE checks
(
    id          UUID                        NOT NULL,
    user_id     UUID                        NOT NULL,
    booking_id  UUID,
    trip_id     UUID,
    booking_fee DECIMAL(10, 2)              NOT NULL,
    unlock_fee  DECIMAL(10, 2)              NOT NULL,
    trip_fee    DECIMAL(10, 2)              NOT NULL,
    total       DECIMAL(10, 2)              NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_checks PRIMARY KEY (id)
);

ALTER TABLE trips
    ADD booking_id UUID;

ALTER TABLE checks
    ADD CONSTRAINT uc_checks_booking UNIQUE (booking_id);

ALTER TABLE checks
    ADD CONSTRAINT uc_checks_trip UNIQUE (trip_id);

ALTER TABLE trips
    ADD CONSTRAINT uc_trips_booking UNIQUE (booking_id);

ALTER TABLE checks
    ADD CONSTRAINT FK_CHECKS_ON_BOOKING FOREIGN KEY (booking_id) REFERENCES bookings (id);

ALTER TABLE checks
    ADD CONSTRAINT FK_CHECKS_ON_TRIP FOREIGN KEY (trip_id) REFERENCES trips (id);

ALTER TABLE checks
    ADD CONSTRAINT FK_CHECKS_ON_USER FOREIGN KEY (user_id) REFERENCES app_users (id);

ALTER TABLE trips
    ADD CONSTRAINT FK_TRIPS_ON_BOOKING FOREIGN KEY (booking_id) REFERENCES bookings (id);

ALTER TABLE bookings
    ALTER COLUMN booked_bike_id SET NOT NULL;

ALTER TABLE bookings
    ALTER COLUMN user_id SET NOT NULL;