-- V3__seed_data.sql — Seed data for sla-guard

-- ═══════ SLA RECORDS ═══════
INSERT INTO sla_record (id, ticket_id, branch_id, sla_type, deadline, status, resolved_at) VALUES
-- Mumbai tickets
('c1d2e3f4-0001-4000-8000-000000000001', 'd1e2f3a4-0001-4000-8000-000000000001', 'a1b2c3d4-0001-4000-8000-000000000001', 'STANDARD_48H', now() - interval '3 days', 'RESOLVED', now() - interval '4 days'),
('c1d2e3f4-0001-4000-8000-000000000002', 'd1e2f3a4-0001-4000-8000-000000000002', 'a1b2c3d4-0001-4000-8000-000000000001', 'STANDARD_48H', now() + interval '2 days', 'ACTIVE', NULL),
('c1d2e3f4-0001-4000-8000-000000000003', 'd1e2f3a4-0001-4000-8000-000000000003', 'a1b2c3d4-0001-4000-8000-000000000001', 'URGENT_24H', now() - interval '1 hour', 'BREACHED', NULL),
('c1d2e3f4-0001-4000-8000-000000000004', 'd1e2f3a4-0001-4000-8000-000000000005', 'a1b2c3d4-0001-4000-8000-000000000001', 'URGENT_24H', now() + interval '12 hours', 'ACTIVE', NULL),
-- Pune tickets
('c1d2e3f4-0001-4000-8000-000000000005', 'd1e2f3a4-0001-4000-8000-000000000007', 'a1b2c3d4-0001-4000-8000-000000000002', 'STANDARD_48H', now() + interval '1 day', 'ACTIVE', NULL),
('c1d2e3f4-0001-4000-8000-000000000006', 'd1e2f3a4-0001-4000-8000-000000000008', 'a1b2c3d4-0001-4000-8000-000000000002', 'STANDARD_48H', now() - interval '2 days', 'RESOLVED', now() - interval '3 days'),
-- Nagpur tickets
('c1d2e3f4-0001-4000-8000-000000000007', 'd1e2f3a4-0001-4000-8000-000000000011', 'a1b2c3d4-0001-4000-8000-000000000003', 'STANDARD_48H', now() - interval '1 day', 'RESOLVED', now() - interval '2 days'),
('c1d2e3f4-0001-4000-8000-000000000008', 'd1e2f3a4-0001-4000-8000-000000000014', 'a1b2c3d4-0001-4000-8000-000000000003', 'URGENT_24H', now() - interval '2 hours', 'BREACHED', NULL);

-- ═══════ COMPLAINTS ═══════
INSERT INTO complaint (ticket_id, customer_id, branch_id, description, status, resolution) VALUES
('d1e2f3a4-0001-4000-8000-000000000003', 'c1d2e3f4-0001-4000-8000-000000000006', 'a1b2c3d4-0001-4000-8000-000000000001', 'Repair taking too long, SLA breached. Device needed urgently for work.', 'OPEN', NULL),
('d1e2f3a4-0001-4000-8000-000000000014', 'c1d2e3f4-0001-4000-8000-000000000015', 'a1b2c3d4-0001-4000-8000-000000000003', 'Urgent repair marked but not prioritized. Hard drive data at risk.', 'OPEN', NULL),
('d1e2f3a4-0001-4000-8000-000000000008', 'c1d2e3f4-0001-4000-8000-000000000005', 'a1b2c3d4-0001-4000-8000-000000000002', 'Speaker was replaced but still has intermittent crackling.', 'RESOLVED', 'Technician rechecked — loose ribbon cable was fixed. Issue resolved with no extra charge.');

-- ═══════ WARRANTIES ═══════
INSERT INTO warranty (ticket_id, customer_id, branch_id, category, warranty_days, start_date, expiry_date, status, description) VALUES
('d1e2f3a4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000001', 'a1b2c3d4-0001-4000-8000-000000000001', 'STANDARD', 60, now() - interval '5 days', now() + interval '55 days', 'ACTIVE', 'DC board replacement — 60-day workmanship warranty'),
('d1e2f3a4-0001-4000-8000-000000000008', 'c1d2e3f4-0001-4000-8000-000000000005', 'a1b2c3d4-0001-4000-8000-000000000002', 'QUICK_FIX', 30, now() - interval '3 days', now() + interval '27 days', 'ACTIVE', 'Speaker module replacement — 30-day warranty'),
('d1e2f3a4-0001-4000-8000-000000000011', 'c1d2e3f4-0001-4000-8000-000000000003', 'a1b2c3d4-0001-4000-8000-000000000003', 'STANDARD', 60, now() - interval '1 day', now() + interval '59 days', 'ACTIVE', 'Battery replacement — 60-day warranty'),
('d1e2f3a4-0001-4000-8000-000000000010', 'c1d2e3f4-0001-4000-8000-000000000014', 'a1b2c3d4-0001-4000-8000-000000000002', 'STANDARD', 60, now(), now() + interval '60 days', 'ACTIVE', 'RAM replacement — 60-day warranty');

-- ═══════ SLA CREDITS ═══════
INSERT INTO sla_credit (ticket_id, customer_id, branch_id, sla_record_id, credit_amount, reason) VALUES
('d1e2f3a4-0001-4000-8000-000000000003', 'c1d2e3f4-0001-4000-8000-000000000006', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000003', 500.00, 'SLA breach compensation — urgent repair exceeded 24-hour deadline');
