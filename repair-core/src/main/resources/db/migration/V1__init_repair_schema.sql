-- V1__init_repair_schema.sql
-- Repair core service database schema

CREATE TABLE repair_ticket (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    assigned_tech_id UUID,
    device_type VARCHAR(30) NOT NULL,
    device_model VARCHAR(128),
    device_serial VARCHAR(64),
    symptom TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(10) NOT NULL DEFAULT 'NORMAL',
    estimated_cost NUMERIC(12, 2),
    final_cost NUMERIC(12, 2),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    completed_at TIMESTAMP
);

CREATE TABLE diagnosis_step (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL REFERENCES repair_ticket(id) ON DELETE CASCADE,
    step_order INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    result VARCHAR(64),
    performed_by UUID,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE ticket_timeline (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL REFERENCES repair_ticket(id) ON DELETE CASCADE,
    action VARCHAR(64) NOT NULL,
    detail TEXT,
    performed_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Indexes for common queries
CREATE INDEX idx_ticket_branch_id ON repair_ticket(branch_id);
CREATE INDEX idx_ticket_customer_id ON repair_ticket(customer_id);
CREATE INDEX idx_ticket_status ON repair_ticket(status);
CREATE INDEX idx_ticket_assigned_tech ON repair_ticket(assigned_tech_id);
CREATE INDEX idx_ticket_created_at ON repair_ticket(created_at);
CREATE INDEX idx_diagnosis_ticket_id ON diagnosis_step(ticket_id);
CREATE INDEX idx_timeline_ticket_id ON ticket_timeline(ticket_id);
