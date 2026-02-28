-- V1__init_parts_schema.sql

CREATE TABLE spare_part (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sku VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(30),
    cost_price NUMERIC(12, 2),
    retail_price NUMERIC(12, 2),
    is_central BOOLEAN DEFAULT false,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE branch_inventory (
    branch_id UUID NOT NULL,
    spare_part_id UUID NOT NULL REFERENCES spare_part(id),
    qty INT DEFAULT 0,
    min_stock INT DEFAULT 5,
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (branch_id, spare_part_id)
);

CREATE TABLE procurement_order (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(32) NOT NULL UNIQUE,
    branch_id UUID NOT NULL,
    spare_part_id UUID NOT NULL REFERENCES spare_part(id),
    quantity INT NOT NULL,
    total_cost NUMERIC(12, 2),
    supplier_name VARCHAR(128),
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    expected_delivery TIMESTAMP,
    delivered_at TIMESTAMP
);

CREATE INDEX idx_inv_branch ON branch_inventory(branch_id);
CREATE INDEX idx_order_branch ON procurement_order(branch_id);
CREATE INDEX idx_order_status ON procurement_order(status);
