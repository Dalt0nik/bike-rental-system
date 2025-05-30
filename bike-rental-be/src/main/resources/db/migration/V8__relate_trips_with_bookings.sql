ALTER TABLE trips
    ADD booking_id UUID;

ALTER TABLE trips
    ADD CONSTRAINT uc_trips_booking UNIQUE (booking_id);

ALTER TABLE trips
    ADD CONSTRAINT FK_TRIPS_ON_BOOKING FOREIGN KEY (booking_id) REFERENCES bookings (id);

ALTER TABLE bookings
DROP
COLUMN is_active;