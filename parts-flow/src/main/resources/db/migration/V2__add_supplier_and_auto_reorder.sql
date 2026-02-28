-- V2__add_supplier_and_auto_reorder.sql

CREATE TABLE supplier (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(128) NOT NULL,
    contact_person VARCHAR(128),
    email VARCHAR(128),
    phone VARCHAR(20),
    address TEXT,
    delivery_sla_hours INT DEFAULT 72,
    rating DOUBLE PRECISION DEFAULT 3.0,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Add supplier FK to procurement_order
ALTER TABLE procurement_order ADD COLUMN supplier_id UUID REFERENCES supplier(id);

-- Add re-order threshold to branch_inventory
ALTER TABLE branch_inventory ADD COLUMN auto_reorder BOOLEAN DEFAULT true;

CREATE INDEX idx_supplier_active ON supplier(active);
