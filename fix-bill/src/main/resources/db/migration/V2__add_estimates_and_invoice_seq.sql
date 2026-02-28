-- V2__add_estimates_and_invoice_seq.sql

-- Estimate / quote table
CREATE TABLE estimate (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL,
    branch_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    labor_cost NUMERIC(12, 2) NOT NULL,
    parts_cost NUMERIC(12, 2) NOT NULL,
    tax_rate NUMERIC(5, 2) DEFAULT 18.00,
    tax_amount NUMERIC(12, 2) NOT NULL,
    total_estimate NUMERIC(12, 2) NOT NULL,
    estimated_days INT DEFAULT 2,
    work_description TEXT,
    status VARCHAR(30) DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    approved_at TIMESTAMP
);

-- Persistent sequence for invoice numbers so they survive restarts
CREATE SEQUENCE invoice_number_seq START WITH 1000 INCREMENT BY 1;

CREATE INDEX idx_estimate_ticket ON estimate(ticket_id);
CREATE INDEX idx_estimate_branch ON estimate(branch_id);
CREATE INDEX idx_estimate_status ON estimate(status);
