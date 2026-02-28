-- V2__seed_data.sql — Seed data for franchise-hub

-- ═══════ FRANCHISES ═══════
INSERT INTO franchise (id, branch_id, owner_name, business_name, email, phone, tier, status, setup_fee, royalty_percent) VALUES
('j1k2l3m4-0001-4000-8000-000000000001', 'a1b2c3d4-0001-4000-8000-000000000001', 'Manoj Tiwari', 'RepairBro Mumbai Pvt Ltd', 'manoj@repairbromumbai.in', '9800200001', 'PREMIUM', 'ACTIVE', 500000.00, 7.00),
('j1k2l3m4-0001-4000-8000-000000000002', 'a1b2c3d4-0001-4000-8000-000000000002', 'Sunita Pawar', 'RepairBro Pune Pvt Ltd', 'sunita@repairbropune.in', '9800200002', 'STANDARD', 'ACTIVE', 300000.00, 7.00),
('j1k2l3m4-0001-4000-8000-000000000003', 'a1b2c3d4-0001-4000-8000-000000000003', 'Vijay Wankhede', 'RepairBro Nagpur Pvt Ltd', 'vijay@repairbronagpur.in', '9800200003', 'STANDARD', 'ACTIVE', 250000.00, 7.00);

-- ═══════ ROYALTY RECORDS ═══════
INSERT INTO royalty_record (franchise_id, branch_id, period, gross_revenue, royalty_percent, royalty_amount, payment_status) VALUES
-- Jan 2024
('j1k2l3m4-0001-4000-8000-000000000001', 'a1b2c3d4-0001-4000-8000-000000000001', '2024-01', 285000.00, 7.00, 19950.00, 'PAID'),
('j1k2l3m4-0001-4000-8000-000000000002', 'a1b2c3d4-0001-4000-8000-000000000002', '2024-01', 165000.00, 7.00, 11550.00, 'PAID'),
('j1k2l3m4-0001-4000-8000-000000000003', 'a1b2c3d4-0001-4000-8000-000000000003', '2024-01', 98000.00, 7.00, 6860.00, 'PAID'),
-- Feb 2024
('j1k2l3m4-0001-4000-8000-000000000001', 'a1b2c3d4-0001-4000-8000-000000000001', '2024-02', 310000.00, 7.00, 21700.00, 'PAID'),
('j1k2l3m4-0001-4000-8000-000000000002', 'a1b2c3d4-0001-4000-8000-000000000002', '2024-02', 178000.00, 7.00, 12460.00, 'PENDING'),
('j1k2l3m4-0001-4000-8000-000000000003', 'a1b2c3d4-0001-4000-8000-000000000003', '2024-02', 115000.00, 7.00, 8050.00, 'PENDING');
