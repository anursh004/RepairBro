-- V4__group_rbac_schema.sql
-- Group-based RBAC: permissions, groups, user-group mapping, user-location mapping

-- ═══════ PERMISSION ═══════
CREATE TABLE permission (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(80) NOT NULL UNIQUE,
    name        VARCHAR(120) NOT NULL,
    module      VARCHAR(40) NOT NULL,
    http_method VARCHAR(10),
    api_pattern VARCHAR(200),
    description VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_permission_module ON permission(module);
CREATE INDEX idx_permission_code   ON permission(code);

-- ═══════ PERMISSION GROUP ═══════
CREATE TABLE permission_group (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(80) NOT NULL UNIQUE,
    description     VARCHAR(500),
    level           VARCHAR(30) NOT NULL DEFAULT 'OPERATIONAL',
    system_defined  BOOLEAN NOT NULL DEFAULT FALSE,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_pgroup_level  ON permission_group(level);
CREATE INDEX idx_pgroup_active ON permission_group(active);

-- ═══════ GROUP ↔ PERMISSION (Many-to-Many) ═══════
CREATE TABLE group_permissions (
    group_id      UUID NOT NULL REFERENCES permission_group(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permission(id) ON DELETE CASCADE,
    PRIMARY KEY (group_id, permission_id)
);

-- ═══════ USER ↔ GROUP (Many-to-Many) ═══════
CREATE TABLE user_groups (
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id    UUID NOT NULL REFERENCES permission_group(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP NOT NULL DEFAULT now(),
    assigned_by UUID,
    PRIMARY KEY (user_id, group_id)
);

CREATE INDEX idx_ug_user  ON user_groups(user_id);
CREATE INDEX idx_ug_group ON user_groups(group_id);

-- ═══════ USER ↔ LOCATION (Many-to-Many) ═══════
CREATE TABLE user_locations (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    branch_id        UUID NOT NULL,
    primary_location BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (user_id, branch_id)
);

CREATE INDEX idx_ul_user   ON user_locations(user_id);
CREATE INDEX idx_ul_branch ON user_locations(branch_id);
