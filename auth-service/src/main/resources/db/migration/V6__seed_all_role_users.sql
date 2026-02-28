-- V6__seed_all_role_users.sql
-- Seed sample users for EVERY permission group with user_groups + user_locations
-- All passwords: Password@123  (bcrypt hash below)
-- Existing V3 users already mapped to groups via V5 migration

-- bcrypt('Password@123') with strength 12
-- $2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2

-- Branch IDs (from repair-core seed):
--   Mumbai:  a1b2c3d4-0001-4000-8000-000000000001
--   Pune:    a1b2c3d4-0001-4000-8000-000000000002
--   Nagpur:  a1b2c3d4-0001-4000-8000-000000000003

-- Group IDs (from V5):
--   Super Admin:             10000000-0001-4000-b000-000000000001
--   Regional Manager:        10000000-0001-4000-b000-000000000002
--   Service Manager:         10000000-0001-4000-b000-000000000003
--   Service Senior Exec:     10000000-0001-4000-b000-000000000004
--   Service Executive:       10000000-0001-4000-b000-000000000005
--   Service Intern:          10000000-0001-4000-b000-000000000006
--   Store Counter Executive: 10000000-0001-4000-b000-000000000007
--   Billing Admin:           10000000-0001-4000-b000-000000000008
--   Billing Viewer:          10000000-0001-4000-b000-000000000009
--   Inventory Manager:       10000000-0001-4000-b000-00000000000a
--   Franchise Owner:         10000000-0001-4000-b000-00000000000b
--   Customer:                10000000-0001-4000-b000-00000000000c


-- ═══════════════════════════════════════════════════════
-- USERS (12 — one per group)
-- ═══════════════════════════════════════════════════════

INSERT INTO users (id, email, password_hash, full_name, phone, status, branch_id) VALUES
-- 1. Super Admin
('c0000000-0001-4000-9000-000000000001', 'superadmin@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Vikram Chauhan', '9900000001', 'ACTIVE', NULL),
-- 2. Regional Manager
('c0000000-0001-4000-9000-000000000002', 'regional.mgr@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Anaya Sharma', '9900000002', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
-- 3. Service Manager
('c0000000-0001-4000-9000-000000000003', 'service.mgr@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Rohit Kulkarni', '9900000003', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
-- 4. Service Senior Executive
('c0000000-0001-4000-9000-000000000004', 'senior.exec@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Priya Nair', '9900000004', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
-- 5. Service Executive
('c0000000-0001-4000-9000-000000000005', 'service.exec@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Amit Verma', '9900000005', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
-- 6. Service Intern
('c0000000-0001-4000-9000-000000000006', 'intern@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Sneha Gupta', '9900000006', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000002'),
-- 7. Store Counter Executive
('c0000000-0001-4000-9000-000000000007', 'counter.exec@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Ravi Deshmukh', '9900000007', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
-- 8. Billing Admin
('c0000000-0001-4000-9000-000000000008', 'billing.admin@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Kavita Joshi', '9900000008', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
-- 9. Billing Viewer
('c0000000-0001-4000-9000-000000000009', 'billing.viewer@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Deepa Rao', '9900000009', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000002'),
-- 10. Inventory Manager
('c0000000-0001-4000-9000-000000000010', 'inventory.mgr@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Siddharth Patil', '9900000010', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
-- 11. Franchise Owner
('c0000000-0001-4000-9000-000000000011', 'franchise.owner@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Manisha Patel', '9900000011', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
-- 12. Customer
('c0000000-0001-4000-9000-000000000012', 'customer@repairbro.in',
 '$2a$12$LJ3dMwrGPCVBsYnQ8YqpyOGExXIMiSWz7FNkxjQHh3RGLM.sN7Pf2',
 'Arjun Mehta', '9900000012', 'ACTIVE', NULL)
ON CONFLICT (email) DO NOTHING;


-- ═══════════════════════════════════════════════════════
-- USER → GROUP ASSIGNMENTS
-- ═══════════════════════════════════════════════════════

INSERT INTO user_groups (user_id, group_id) VALUES
-- Super Admin
('c0000000-0001-4000-9000-000000000001', '10000000-0001-4000-b000-000000000001'),
-- Regional Manager
('c0000000-0001-4000-9000-000000000002', '10000000-0001-4000-b000-000000000002'),
-- Service Manager
('c0000000-0001-4000-9000-000000000003', '10000000-0001-4000-b000-000000000003'),
-- Service Senior Executive
('c0000000-0001-4000-9000-000000000004', '10000000-0001-4000-b000-000000000004'),
-- Service Executive
('c0000000-0001-4000-9000-000000000005', '10000000-0001-4000-b000-000000000005'),
-- Service Intern
('c0000000-0001-4000-9000-000000000006', '10000000-0001-4000-b000-000000000006'),
-- Store Counter Executive
('c0000000-0001-4000-9000-000000000007', '10000000-0001-4000-b000-000000000007'),
-- Billing Admin
('c0000000-0001-4000-9000-000000000008', '10000000-0001-4000-b000-000000000008'),
-- Billing Viewer
('c0000000-0001-4000-9000-000000000009', '10000000-0001-4000-b000-000000000009'),
-- Inventory Manager
('c0000000-0001-4000-9000-000000000010', '10000000-0001-4000-b000-00000000000a'),
-- Franchise Owner
('c0000000-0001-4000-9000-000000000011', '10000000-0001-4000-b000-00000000000b'),
-- Customer
('c0000000-0001-4000-9000-000000000012', '10000000-0001-4000-b000-00000000000c')
ON CONFLICT DO NOTHING;


-- ═══════════════════════════════════════════════════════
-- USER → LOCATION ASSIGNMENTS
-- ═══════════════════════════════════════════════════════

-- Super Admin → all 3 branches (primary: Mumbai)
INSERT INTO user_locations (user_id, branch_id, primary_location) VALUES
('c0000000-0001-4000-9000-000000000001', 'a1b2c3d4-0001-4000-8000-000000000001', TRUE),
('c0000000-0001-4000-9000-000000000001', 'a1b2c3d4-0001-4000-8000-000000000002', FALSE),
('c0000000-0001-4000-9000-000000000001', 'a1b2c3d4-0001-4000-8000-000000000003', FALSE)
ON CONFLICT DO NOTHING;

-- Regional Manager → Mumbai (primary) + Pune
INSERT INTO user_locations (user_id, branch_id, primary_location) VALUES
('c0000000-0001-4000-9000-000000000002', 'a1b2c3d4-0001-4000-8000-000000000001', TRUE),
('c0000000-0001-4000-9000-000000000002', 'a1b2c3d4-0001-4000-8000-000000000002', FALSE)
ON CONFLICT DO NOTHING;

-- Service Manager → Mumbai
INSERT INTO user_locations (user_id, branch_id, primary_location) VALUES
('c0000000-0001-4000-9000-000000000003', 'a1b2c3d4-0001-4000-8000-000000000001', TRUE)
ON CONFLICT DO NOTHING;

-- Senior Exec → Mumbai
INSERT INTO user_locations (user_id, branch_id, primary_location) VALUES
('c0000000-0001-4000-9000-000000000004', 'a1b2c3d4-0001-4000-8000-000000000001', TRUE)
ON CONFLICT DO NOTHING;

-- Service Exec → Mumbai
INSERT INTO user_locations (user_id, branch_id, primary_location) VALUES
('c0000000-0001-4000-9000-000000000005', 'a1b2c3d4-0001-4000-8000-000000000001', TRUE)
ON CONFLICT DO NOTHING;

-- Intern → Pune
INSERT INTO user_locations (user_id, branch_id, primary_location) VALUES
('c0000000-0001-4000-9000-000000000006', 'a1b2c3d4-0001-4000-8000-000000000002', TRUE)
ON CONFLICT DO NOTHING;

-- Counter Exec → Mumbai
INSERT INTO user_locations (user_id, branch_id, primary_location) VALUES
('c0000000-0001-4000-9000-000000000007', 'a1b2c3d4-0001-4000-8000-000000000001', TRUE)
ON CONFLICT DO NOTHING;

-- Billing Admin → Mumbai (primary) + Pune
INSERT INTO user_locations (user_id, branch_id, primary_location) VALUES
('c0000000-0001-4000-9000-000000000008', 'a1b2c3d4-0001-4000-8000-000000000001', TRUE),
('c0000000-0001-4000-9000-000000000008', 'a1b2c3d4-0001-4000-8000-000000000002', FALSE)
ON CONFLICT DO NOTHING;

-- Billing Viewer → Pune
INSERT INTO user_locations (user_id, branch_id, primary_location) VALUES
('c0000000-0001-4000-9000-000000000009', 'a1b2c3d4-0001-4000-8000-000000000002', TRUE)
ON CONFLICT DO NOTHING;

-- Inventory Manager → all 3 branches
INSERT INTO user_locations (user_id, branch_id, primary_location) VALUES
('c0000000-0001-4000-9000-000000000010', 'a1b2c3d4-0001-4000-8000-000000000001', TRUE),
('c0000000-0001-4000-9000-000000000010', 'a1b2c3d4-0001-4000-8000-000000000002', FALSE),
('c0000000-0001-4000-9000-000000000010', 'a1b2c3d4-0001-4000-8000-000000000003', FALSE)
ON CONFLICT DO NOTHING;

-- Franchise Owner → Mumbai
INSERT INTO user_locations (user_id, branch_id, primary_location) VALUES
('c0000000-0001-4000-9000-000000000011', 'a1b2c3d4-0001-4000-8000-000000000001', TRUE)
ON CONFLICT DO NOTHING;

-- Customer → no location (external)


-- ═══════════════════════════════════════════════════════
-- Also give legacy roles for backwards compat
-- ═══════════════════════════════════════════════════════

INSERT INTO user_roles (user_id, role) VALUES
('c0000000-0001-4000-9000-000000000001', 'ADMIN'),
('c0000000-0001-4000-9000-000000000002', 'BRANCH_MANAGER'),
('c0000000-0001-4000-9000-000000000003', 'BRANCH_MANAGER'),
('c0000000-0001-4000-9000-000000000004', 'TECH'),
('c0000000-0001-4000-9000-000000000005', 'TECH'),
('c0000000-0001-4000-9000-000000000006', 'TECH'),
('c0000000-0001-4000-9000-000000000007', 'TECH'),
('c0000000-0001-4000-9000-000000000008', 'BRANCH_MANAGER'),
('c0000000-0001-4000-9000-000000000009', 'TECH'),
('c0000000-0001-4000-9000-000000000010', 'BRANCH_MANAGER'),
('c0000000-0001-4000-9000-000000000011', 'FRANCHISEE'),
('c0000000-0001-4000-9000-000000000012', 'CUSTOMER')
ON CONFLICT DO NOTHING;
