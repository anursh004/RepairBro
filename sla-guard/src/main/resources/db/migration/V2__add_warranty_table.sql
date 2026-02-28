-- V2__add_warranty_table.sql

CREATE TABLE warranty (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    branch_id UUID NOT NULL,
    category VARCHAR(20) NOT NULL,
    warranty_days INT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    description TEXT,
    claim_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Credit record for SLA breach compensation
CREATE TABLE sla_credit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    branch_id UUID NOT NULL,
    sla_record_id UUID REFERENCES sla_record(id),
    credit_amount NUMERIC(12, 2) NOT NULL,
    reason TEXT,
    redeemed BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_warranty_ticket ON warranty(ticket_id);
CREATE INDEX idx_warranty_customer ON warranty(customer_id);
CREATE INDEX idx_warranty_status ON warranty(status);
CREATE INDEX idx_warranty_expiry ON warranty(expiry_date);
CREATE INDEX idx_credit_customer ON sla_credit(customer_id);
