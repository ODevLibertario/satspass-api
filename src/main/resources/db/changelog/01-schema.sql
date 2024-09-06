--liquibase formatted sql

--changeset odevlibertario:00-initial-schema
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

CREATE TYPE satspass.event_status AS ENUM ('DRAFT', 'PUBLISHED');
CREATE TABLE satspass.event (
    id uuid PRIMARY KEY,
    manager_id UUID REFERENCES satspass.user(id) NOT NULL,
    name TEXT NOT NULL,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    location TEXT NULL,
    publicity_image_url TEXT NULL,
    status satspass.event_status NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE satspass.ticket_category(
    id UUID PRIMARY KEY,
    event_id UUID REFERENCES satspass.event(id) NOT NULL,
    category_name TEXT NOT NULL,
    price INT NOT NULL,
    currency TEXT,
    quantity INT NOT NULL,
    sales_start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    sales_end_date TIMESTAMP WITH TIME ZONE NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_columns UNIQUE (event_id, category_name)
);

CREATE TYPE satspass.ticket_status AS ENUM('RESERVED', 'PURCHASED', 'USED', 'REFUNDED');

CREATE TABLE satspass.ticket(
    id UUID PRIMARY KEY,
    event_id UUID REFERENCES satspass.event(id) NOT NULL,
    ticket_category_id UUID REFERENCES satspass.ticket_category(id) NOT NULL,
    user_id UUID REFERENCES satspass.user(id) NOT NULL,
    qr_code TEXT NULL,
    status satspass.ticket_status NOT NULL,
    payment_hash TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);










