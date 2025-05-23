CREATE TABLE trips
(
    id          UUID                        NOT NULL,
    bike_id     UUID                        NOT NULL,
    user_id     UUID                        NOT NULL,
    start_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    finish_time TIMESTAMP WITHOUT TIME ZONE,
    state       VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_trips PRIMARY KEY (id)
);

ALTER TABLE trips
    ADD CONSTRAINT FK_TRIPS_ON_BIKE FOREIGN KEY (bike_id) REFERENCES bikes (id);

ALTER TABLE trips
    ADD CONSTRAINT FK_TRIPS_ON_USER FOREIGN KEY (user_id) REFERENCES app_users (id);
