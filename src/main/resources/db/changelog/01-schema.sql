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

--changeset odevlibertario:03-ticket-category
CREATE TABLE satspass.ticket_category(
    id UUID PRIMARY KEY,
    event_id UUID,
    category_name TEXT,
    price INT,
    currency TEXT,
    quantity INT,
    sales_start_date TIMESTAMP WITH TIME ZONE,
    sales_end_date TIMESTAMP WITH TIME ZONE NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_columns UNIQUE (event_id, category_name)
);

--changeset odevlibertario:04-ticket-category-constraint
ALTER TABLE satspass.ticket_category
ADD CONSTRAINT fk_ticket_category_event
FOREIGN KEY (event_id)
REFERENCES satspass.event(id);

--changeset odevlibertario:05-ticket-category-constraint
CREATE TYPE satspass.ticket_status AS ENUM('RESERVED', 'PURCHASED', 'USED', 'REFUNDED');
CREATE TABLE satspass.ticket(
    id UUID PRIMARY KEY,
    event_id UUID REFERENCES satspass.event(id),
    ticket_category_id UUID REFERENCES satspass.ticket_category(id),
    user_id UUID REFERENCES satspass.user(id),
    qr_code TEXT,
    status satspass.ticket_status,
    payment_hash TEXT
);









