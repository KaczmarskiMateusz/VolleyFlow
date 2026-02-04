CREATE SCHEMA IF NOT EXISTS app;

CREATE TABLE IF NOT EXISTS app.club_member
(
    id                         BIGSERIAL PRIMARY KEY,
    external_id                UUID         NOT NULL UNIQUE,

    club_id                    BIGINT       NOT NULL,
    user_account_id            BIGINT       NULL,

    role                       VARCHAR(20)  NOT NULL,
    status                     VARCHAR(20)  NOT NULL,

    invited_email              VARCHAR(320) NULL,
    invite_token               VARCHAR(128) NULL UNIQUE,
    invite_expires_at          TIMESTAMPTZ  NULL,
    invite_accepted_at         TIMESTAMPTZ  NULL,

    invited_by_user_account_id BIGINT       NULL,

    joined_at                  TIMESTAMPTZ  NULL,

    created_at                 TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at                 TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    version                    INTEGER      NOT NULL DEFAULT 0,

    CONSTRAINT fk_club_member_club
        FOREIGN KEY (club_id)
            REFERENCES app.club (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_club_member_user
        FOREIGN KEY (user_account_id)
            REFERENCES app.user_account (id)
            ON DELETE SET NULL,

    CONSTRAINT fk_club_member_invited_by_user
        FOREIGN KEY (invited_by_user_account_id)
            REFERENCES app.user_account (id)
            ON DELETE RESTRICT,

    CONSTRAINT uk_club_member_club_invited_email
        UNIQUE (club_id, invited_email)
);

CREATE INDEX IF NOT EXISTS idx_club_member_external_id    ON app.club_member (external_id);
CREATE INDEX IF NOT EXISTS idx_club_member_club_id        ON app.club_member (club_id);
CREATE INDEX IF NOT EXISTS idx_club_member_user_id        ON app.club_member (user_account_id);
CREATE INDEX IF NOT EXISTS idx_club_member_invited_email  ON app.club_member (invited_email);
CREATE INDEX IF NOT EXISTS idx_club_member_status         ON app.club_member (status);
CREATE INDEX IF NOT EXISTS idx_club_member_invite_token   ON app.club_member (invite_token);
