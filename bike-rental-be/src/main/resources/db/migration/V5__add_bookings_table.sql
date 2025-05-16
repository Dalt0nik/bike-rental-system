CREATE TABLE bookings
(
    id             UUID NOT NULL,
    user_id        UUID,
    booked_bike_id UUID,
    CONSTRAINT pk_bookings PRIMARY KEY (id)
);

ALTER TABLE bookings
    ADD CONSTRAINT FK_BOOKINGS_ON_BOOKED_BIKE FOREIGN KEY (booked_bike_id) REFERENCES bikes (id);

ALTER TABLE bookings
    ADD CONSTRAINT FK_BOOKINGS_ON_USER FOREIGN KEY (user_id) REFERENCES app_users (id);