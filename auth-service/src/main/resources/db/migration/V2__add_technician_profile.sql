-- V2__add_technician_profile.sql
-- Technician extended profile for skill tracking & performance metrics

CREATE TABLE technician_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    branch_id UUID NOT NULL,
    skill_level VARCHAR(20) NOT NULL DEFAULT 'JUNIOR',
    specializations TEXT,
    certifications TEXT,
    hourly_rate NUMERIC(8, 2) DEFAULT 0,
    tickets_resolved INT DEFAULT 0,
    avg_repair_time_hours DOUBLE PRECISION DEFAULT 0,
    first_time_fix_rate DOUBLE PRECISION DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_tech_user ON technician_profile(user_id);
CREATE INDEX idx_tech_branch ON technician_profile(branch_id);
CREATE INDEX idx_tech_skill ON technician_profile(skill_level);
