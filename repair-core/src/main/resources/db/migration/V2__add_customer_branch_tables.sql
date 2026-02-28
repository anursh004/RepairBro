-- V2__add_customer_branch_tables.sql
-- Adds Customer and Branch entities to the repair-core service

CREATE TABLE customer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(128) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(128),
    address TEXT,
    city VARCHAR(64),
    pincode VARCHAR(10),
    preferred_channel VARCHAR(20) DEFAULT 'SMS',
    total_repairs INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE branch (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(128) NOT NULL,
    city VARCHAR(64) NOT NULL,
    tier INT NOT NULL DEFAULT 2,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(128),
    monthly_rent NUMERIC(12, 2),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Add FK constraints to repair_ticket (existing table)
ALTER TABLE repair_ticket
    ADD CONSTRAINT fk_ticket_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    ADD CONSTRAINT fk_ticket_branch FOREIGN KEY (branch_id) REFERENCES branch(id);

-- Indexes
CREATE INDEX idx_customer_phone ON customer(phone);
CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_customer_city ON customer(city);
CREATE INDEX idx_branch_city ON branch(city);
CREATE INDEX idx_branch_tier ON branch(tier);
CREATE INDEX idx_branch_active ON branch(active);
