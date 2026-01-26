CREATE SCHEMA IF NOT EXISTS app;

CREATE TABLE IF NOT EXISTS app.club
(
    id          BIGSERIAL PRIMARY KEY,
    external_id UUID        NOT NULL UNIQUE,
    name        VARCHAR(160) NOT NULL,
    description VARCHAR(2000),
    city        VARCHAR(120),
    logo_url    VARCHAR(2048),
    status      VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    version     INTEGER     NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS ix_club_external_id ON app.club (external_id);
CREATE INDEX IF NOT EXISTS ix_club_name        ON app.club (name);
CREATE INDEX IF NOT EXISTS ix_club_status      ON app.club (status);