-- V1__init_sla_schema.sql

CREATE TABLE sla_record (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL,
    branch_id UUID NOT NULL,
    sla_type VARCHAR(30) NOT NULL,
    deadline TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    resolved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE complaint (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    branch_id UUID NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'OPEN',
    resolution TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    resolved_at TIMESTAMP
);

CREATE INDEX idx_sla_ticket ON sla_record(ticket_id);
CREATE INDEX idx_sla_branch ON sla_record(branch_id);
CREATE INDEX idx_sla_status ON sla_record(status);
CREATE INDEX idx_complaint_branch ON complaint(branch_id);
CREATE INDEX idx_complaint_ticket ON complaint(ticket_id);
