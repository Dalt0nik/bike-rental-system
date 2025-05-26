ALTER TABLE trips
    ADD booking_id UUID;

ALTER TABLE bookings
    ADD trip_id UUID;

ALTER TABLE bookings
    ADD CONSTRAINT uc_bookings_trip UNIQUE (trip_id);

ALTER TABLE trips
    ADD CONSTRAINT uc_trips_booking UNIQUE (booking_id);

ALTER TABLE bookings
    ADD CONSTRAINT FK_BOOKINGS_ON_TRIP FOREIGN KEY (trip_id) REFERENCES trips (id);

ALTER TABLE trips
    ADD CONSTRAINT FK_TRIPS_ON_BOOKING FOREIGN KEY (booking_id) REFERENCES bookings (id);