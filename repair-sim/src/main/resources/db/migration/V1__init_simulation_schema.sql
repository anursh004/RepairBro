-- V1__init_simulation_schema.sql

CREATE TABLE simulation_scenario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(128) NOT NULL,
    branch_count INT NOT NULL DEFAULT 3,
    avg_demands_per_day INT NOT NULL DEFAULT 8,
    simulation_days INT NOT NULL DEFAULT 30,
    junior_tech_ratio DOUBLE PRECISION DEFAULT 0.6,
    techs_per_branch INT DEFAULT 3,
    inventory_strategy VARCHAR(20) DEFAULT 'LOCAL',
    city_tier INT DEFAULT 2,
    monthly_rent DOUBLE PRECISION DEFAULT 50000,
    avg_ticket_value DOUBLE PRECISION DEFAULT 1800,
    avg_parts_cost DOUBLE PRECISION DEFAULT 500,
    status VARCHAR(20) DEFAULT 'CREATED',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE simulation_result (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scenario_id UUID NOT NULL REFERENCES simulation_scenario(id) ON DELETE CASCADE,
    branch_index INT NOT NULL,
    total_revenue DOUBLE PRECISION DEFAULT 0,
    total_parts_cost DOUBLE PRECISION DEFAULT 0,
    total_labor_cost DOUBLE PRECISION DEFAULT 0,
    total_rent DOUBLE PRECISION DEFAULT 0,
    total_royalty DOUBLE PRECISION DEFAULT 0,
    net_profit DOUBLE PRECISION DEFAULT 0,
    profit_margin_percent DOUBLE PRECISION DEFAULT 0,
    total_tickets INT DEFAULT 0,
    completed_tickets INT DEFAULT 0,
    sla_breach INT DEFAULT 0,
    first_time_fix_rate DOUBLE PRECISION DEFAULT 0,
    mean_repair_time_hours DOUBLE PRECISION DEFAULT 0,
    tech_utilization_percent DOUBLE PRECISION DEFAULT 0,
    inventory_stockouts INT DEFAULT 0,
    break_even_tickets_per_day INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_sim_result_scenario ON simulation_result(scenario_id);
