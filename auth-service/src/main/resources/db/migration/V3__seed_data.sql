-- V3__seed_data.sql — Production-ready seed data for auth-service

-- ═══════ USERS ═══════
-- Admin
INSERT INTO users (id, email, password_hash, full_name, phone, status, branch_id) VALUES
('b1c2d3e4-0000-4000-8000-000000000001', 'admin@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'System Admin', '9800000000', 'ACTIVE', NULL);
INSERT INTO user_roles (user_id, role) VALUES ('b1c2d3e4-0000-4000-8000-000000000001', 'ADMIN');

-- Branch Managers
INSERT INTO users (id, email, password_hash, full_name, phone, status, branch_id) VALUES
('b1c2d3e4-0000-4000-8000-000000000002', 'manager.mumbai@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Rahul Kapoor', '9800000001', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
('b1c2d3e4-0000-4000-8000-000000000003', 'manager.pune@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Meera Deshpande', '9800000002', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000002'),
('b1c2d3e4-0000-4000-8000-000000000004', 'manager.nagpur@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Sunil Patil', '9800000003', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000003');
INSERT INTO user_roles (user_id, role) VALUES
('b1c2d3e4-0000-4000-8000-000000000002', 'BRANCH_MANAGER'),
('b1c2d3e4-0000-4000-8000-000000000003', 'BRANCH_MANAGER'),
('b1c2d3e4-0000-4000-8000-000000000004', 'BRANCH_MANAGER');

-- Technicians (2 per branch = 6 total)
INSERT INTO users (id, email, password_hash, full_name, phone, status, branch_id) VALUES
-- Mumbai techs
('b1c2d3e4-0001-4000-8000-000000000001', 'arjun.tech@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Arjun Reddy', '9800100001', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
('b1c2d3e4-0001-4000-8000-000000000002', 'kiran.tech@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Kiran Shah', '9800100002', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
-- Pune techs
('b1c2d3e4-0001-4000-8000-000000000003', 'deepak.tech@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Deepak Jadhav', '9800100003', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000002'),
('b1c2d3e4-0001-4000-8000-000000000004', 'revati.tech@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Revati More', '9800100004', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000002'),
-- Nagpur techs
('b1c2d3e4-0001-4000-8000-000000000005', 'sanjay.tech@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Sanjay Bhosale', '9800100005', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000003'),
('b1c2d3e4-0001-4000-8000-000000000006', 'prashant.tech@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Prashant Gaikwad', '9800100006', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000003');
INSERT INTO user_roles (user_id, role) VALUES
('b1c2d3e4-0001-4000-8000-000000000001', 'TECH'),
('b1c2d3e4-0001-4000-8000-000000000002', 'TECH'),
('b1c2d3e4-0001-4000-8000-000000000003', 'TECH'),
('b1c2d3e4-0001-4000-8000-000000000004', 'TECH'),
('b1c2d3e4-0001-4000-8000-000000000005', 'TECH'),
('b1c2d3e4-0001-4000-8000-000000000006', 'TECH');

-- Franchisees
INSERT INTO users (id, email, password_hash, full_name, phone, status, branch_id) VALUES
('b1c2d3e4-0002-4000-8000-000000000001', 'franchisee.mumbai@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Manoj Tiwari', '9800200001', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000001'),
('b1c2d3e4-0002-4000-8000-000000000002', 'franchisee.pune@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Sunita Pawar', '9800200002', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000002'),
('b1c2d3e4-0002-4000-8000-000000000003', 'franchisee.nagpur@repairbro.in', '$2a$10$N1k7y8NP9ASfLWQ6sF0jZeZ3VtLnK8YqHs.wVfX1YzT/nJHgK3fRq', 'Vijay Wankhede', '9800200003', 'ACTIVE', 'a1b2c3d4-0001-4000-8000-000000000003');
INSERT INTO user_roles (user_id, role) VALUES
('b1c2d3e4-0002-4000-8000-000000000001', 'FRANCHISEE'),
('b1c2d3e4-0002-4000-8000-000000000002', 'FRANCHISEE'),
('b1c2d3e4-0002-4000-8000-000000000003', 'FRANCHISEE');

-- ═══════ TECHNICIAN PROFILES ═══════
INSERT INTO technician_profile (user_id, branch_id, skill_level, specializations, certifications, hourly_rate, tickets_resolved, avg_repair_time_hours, first_time_fix_rate) VALUES
('b1c2d3e4-0001-4000-8000-000000000001', 'a1b2c3d4-0001-4000-8000-000000000001', 'SPECIALIST', 'laptop,desktop,board-level', 'Apple Certified Mac Technician,CompTIA A+', 600.00, 342, 2.8, 0.89),
('b1c2d3e4-0001-4000-8000-000000000002', 'a1b2c3d4-0001-4000-8000-000000000001', 'CERTIFIED', 'mobile,tablet', 'Samsung Certified,iFixit Pro', 450.00, 215, 1.9, 0.83),
('b1c2d3e4-0001-4000-8000-000000000003', 'a1b2c3d4-0001-4000-8000-000000000002', 'CERTIFIED', 'laptop,desktop', 'Dell Certified,HP Authorized', 400.00, 178, 3.1, 0.80),
('b1c2d3e4-0001-4000-8000-000000000004', 'a1b2c3d4-0001-4000-8000-000000000002', 'JUNIOR', 'mobile', '', 250.00, 89, 2.1, 0.72),
('b1c2d3e4-0001-4000-8000-000000000005', 'a1b2c3d4-0001-4000-8000-000000000003', 'CERTIFIED', 'laptop,mobile,desktop', 'CompTIA A+', 350.00, 156, 2.5, 0.81),
('b1c2d3e4-0001-4000-8000-000000000006', 'a1b2c3d4-0001-4000-8000-000000000003', 'JUNIOR', 'mobile,tablet', '', 200.00, 62, 2.8, 0.68);
