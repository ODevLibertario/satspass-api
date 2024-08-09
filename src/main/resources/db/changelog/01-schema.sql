--liquibase formatted sql

--changeset odevlibertario:00-user
CREATE SCHEMA satspass;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA satspass;

CREATE TYPE satspass.user_status AS ENUM ('PENDING_EMAIL_CONFIRMATION','ACTIVE', 'INACTIVE');
CREATE TYPE satspass.role AS ENUM ('ADMIN', 'EVENT_MANAGER', 'EVENT_CUSTOMER');

CREATE TABLE satspass.user (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255),
    password_hash VARCHAR(255) NOT NULL,
    balance NUMERIC DEFAULT 0,
    status satspass.user_status NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_email ON satspass.user (email);

CREATE TABLE satspass.user_role (
   user_id UUID REFERENCES satspass.user (id) ON DELETE CASCADE,
   role satspass.role NOT NULL,
   created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
   PRIMARY KEY (user_id, role)
);

CREATE INDEX idx_user_role ON satspass.user_role (user_id);

--changeset odevlibertario:01-event
CREATE TYPE satspass.event_status AS ENUM ('DRAFT', 'PUBLISHED');
CREATE TABLE satspass.event (
    id uuid PRIMARY KEY,
    name TEXT,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    publicity_image_url TEXT NULL,
    status satspass.event_status,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

--changeset odevlibertario:02-event-manager-id
ALTER TABLE satspass.event
ADD COLUMN manager_id UUID NOT NULL;

ALTER TABLE satspass.event
ADD CONSTRAINT fk_user
FOREIGN KEY (manager_id)
REFERENCES satspass.user(id);

