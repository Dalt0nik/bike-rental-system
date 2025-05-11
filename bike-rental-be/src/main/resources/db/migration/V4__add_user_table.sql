CREATE TABLE app_users
(
    id        UUID         NOT NULL,
    auth0_id  VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_app_users PRIMARY KEY (id)
);

ALTER TABLE app_users
    ADD CONSTRAINT uc_app_users_auth0 UNIQUE (auth0_id);