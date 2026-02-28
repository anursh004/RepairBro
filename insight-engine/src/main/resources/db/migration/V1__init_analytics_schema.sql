-- V1__init_analytics_schema.sql

CREATE TABLE branch_kpi_daily (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID NOT NULL,
    date DATE NOT NULL,
    tickets_created INT DEFAULT 0,
    tickets_completed INT DEFAULT 0,
    sl_breaches INT DEFAULT 0,
    revenue NUMERIC(12, 2) DEFAULT 0,
    mttr_hours DOUBLE PRECISION DEFAULT 0,
    ftfr DOUBLE PRECISION DEFAULT 0,
    tech_utilization DOUBLE PRECISION DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE(branch_id, date)
);

CREATE INDEX idx_kpi_branch_date ON branch_kpi_daily(branch_id, date);
CREATE INDEX idx_kpi_date ON branch_kpi_daily(date);
