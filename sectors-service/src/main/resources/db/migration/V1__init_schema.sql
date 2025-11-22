-- Schema initialization for Sectors Management App

-- Drop existing tables (in correct order to respect FK constraints)
DROP TABLE IF EXISTS sector;
DROP TABLE IF EXISTS user_sector_selection;

-- Create tables
CREATE TABLE sector (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        parent_id BIGINT REFERENCES sector(id) ON DELETE SET NULL
);

CREATE TABLE user_sector_selection (
                                       id BIGSERIAL PRIMARY KEY,
                                       username VARCHAR(255) NOT NULL,
                                       sector_id BIGINT NOT NULL REFERENCES sector(id) ON DELETE CASCADE,
                                       UNIQUE (username, sector_id)
);
