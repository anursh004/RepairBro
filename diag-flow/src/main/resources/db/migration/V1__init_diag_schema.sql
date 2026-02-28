-- V1__init_diag_schema.sql

CREATE TABLE diag_flow (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    device_type VARCHAR(30),
    symptom_category VARCHAR(128),
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE diag_flow_step (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    diag_flow_id UUID NOT NULL REFERENCES diag_flow(id) ON DELETE CASCADE,
    step_order INT NOT NULL,
    instruction VARCHAR(255) NOT NULL,
    expected_outcome VARCHAR(64),
    confidence_weight DOUBLE PRECISION DEFAULT 0.5,
    notes TEXT
);

CREATE TABLE diagnosis_evaluation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL,
    diag_flow_id UUID REFERENCES diag_flow(id),
    confidence_score DOUBLE PRECISION DEFAULT 0.0,
    suggested_actions TEXT,
    root_cause TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_diag_flow_device ON diag_flow(device_type);
CREATE INDEX idx_diag_flow_symptom ON diag_flow(symptom_category);
CREATE INDEX idx_eval_ticket ON diagnosis_evaluation(ticket_id);
