ALTER TABLE bikes
    DROP CONSTRAINT uc_bikes_short_unique_name;

ALTER TABLE bikes
    DROP COLUMN short_unique_name;