ALTER TABLE bookings
    ALTER COLUMN booked_bike_id SET NOT NULL;

ALTER TABLE bookings
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE trips
DROP
COLUMN state;