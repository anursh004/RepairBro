-- V1__init_franchise_schema.sql

CREATE TABLE franchise (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID NOT NULL UNIQUE,
    owner_name VARCHAR(128) NOT NULL,
    business_name VARCHAR(128),
    email VARCHAR(128),
    phone VARCHAR(20),
    tier VARCHAR(20) DEFAULT 'STANDARD',
    status VARCHAR(20) DEFAULT 'PENDING',
    setup_fee NUMERIC(12, 2),
    royalty_percent NUMERIC(5, 2) DEFAULT 7.00,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE royalty_record (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    franchise_id UUID NOT NULL REFERENCES franchise(id),
    branch_id UUID NOT NULL,
    period VARCHAR(7) NOT NULL,
    gross_revenue NUMERIC(12, 2),
    royalty_percent NUMERIC(5, 2),
    royalty_amount NUMERIC(12, 2),
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_franchise_branch ON franchise(branch_id);
CREATE INDEX idx_royalty_franchise ON royalty_record(franchise_id);
CREATE INDEX idx_royalty_period ON royalty_record(period);
