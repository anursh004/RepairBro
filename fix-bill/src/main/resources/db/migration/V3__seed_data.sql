-- V3__seed_data.sql — Seed data for fix-bill

-- ═══════ INVOICES ═══════
-- Completed tickets get invoices
INSERT INTO invoice (id, invoice_number, ticket_id, branch_id, customer_id, subtotal, tax_amount, tax_rate, discount, total_amount, status, paid_at) VALUES
('g1h2i3j4-0001-4000-8000-000000000001', 'INV-2024-1001', 'd1e2f3a4-0001-4000-8000-000000000001', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000001', 2711.86, 488.14, 18.00, 0, 3200.00, 'PAID', now() - interval '5 days'),
('g1h2i3j4-0001-4000-8000-000000000002', 'INV-2024-1002', 'd1e2f3a4-0001-4000-8000-000000000008', 'a1b2c3d4-0001-4000-8000-000000000002', 'c1d2e3f4-0001-4000-8000-000000000005', 720.34, 129.66, 18.00, 0, 850.00, 'PAID', now() - interval '3 days'),
('g1h2i3j4-0001-4000-8000-000000000003', 'INV-2024-1003', 'd1e2f3a4-0001-4000-8000-000000000011', 'a1b2c3d4-0001-4000-8000-000000000003', 'c1d2e3f4-0001-4000-8000-000000000003', 889.83, 160.17, 18.00, 0, 1050.00, 'PAID', now() - interval '1 day'),
('g1h2i3j4-0001-4000-8000-000000000004', 'INV-2024-1004', 'd1e2f3a4-0001-4000-8000-000000000010', 'a1b2c3d4-0001-4000-8000-000000000002', 'c1d2e3f4-0001-4000-8000-000000000014', 1144.07, 205.93, 18.00, 0, 1350.00, 'SENT', NULL);

-- ═══════ LINE ITEMS ═══════
INSERT INTO invoice_line_item (invoice_id, description, type, quantity, unit_price, line_total, spare_part_id) VALUES
('g1h2i3j4-0001-4000-8000-000000000001', 'MacBook Air DC Board Replacement', 'PART', 1, 2800.00, 2800.00, 'f1a2b3c4-0001-4000-8000-000000000012'),
('g1h2i3j4-0001-4000-8000-000000000001', 'Board-level repair labor (1.5 hrs)', 'LABOR', 1, 900.00, 900.00, NULL),
('g1h2i3j4-0001-4000-8000-000000000002', 'OnePlus 12 Speaker Module', 'PART', 1, 650.00, 650.00, 'f1a2b3c4-0001-4000-8000-000000000014'),
('g1h2i3j4-0001-4000-8000-000000000002', 'Speaker replacement labor', 'LABOR', 1, 200.00, 200.00, NULL),
('g1h2i3j4-0001-4000-8000-000000000003', 'Xiaomi 14 Battery', 'PART', 1, 700.00, 700.00, NULL),
('g1h2i3j4-0001-4000-8000-000000000003', 'Battery replacement labor', 'LABOR', 1, 350.00, 350.00, NULL),
('g1h2i3j4-0001-4000-8000-000000000004', 'DDR5 8GB RAM Module', 'PART', 1, 2000.00, 2000.00, 'f1a2b3c4-0001-4000-8000-000000000015'),
('g1h2i3j4-0001-4000-8000-000000000004', 'RAM diagnosis and replacement labor', 'LABOR', 1, 400.00, 400.00, NULL);

-- ═══════ PAYMENTS ═══════
INSERT INTO payment (invoice_id, amount, method, status, transaction_id) VALUES
('g1h2i3j4-0001-4000-8000-000000000001', 3200.00, 'UPI', 'COMPLETED', 'UPI-TXN-20240101-001'),
('g1h2i3j4-0001-4000-8000-000000000002', 850.00, 'CARD', 'COMPLETED', 'CARD-TXN-20240103-001'),
('g1h2i3j4-0001-4000-8000-000000000003', 1050.00, 'CASH', 'COMPLETED', 'CASH-REC-20240105-001');

-- ═══════ ESTIMATES ═══════
INSERT INTO estimate (ticket_id, branch_id, customer_id, labor_cost, parts_cost, tax_rate, tax_amount, total_estimate, estimated_days, work_description, status) VALUES
('d1e2f3a4-0001-4000-8000-000000000002', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000004', 1200.00, 7500.00, 18.00, 1566.00, 10266.00, 1, 'iPhone 15 OLED screen replacement with calibration', 'PENDING'),
('d1e2f3a4-0001-4000-8000-000000000005', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000011', 2500.00, 3800.00, 18.00, 1134.00, 7434.00, 3, 'Samsung S24 board-level water damage repair', 'APPROVED'),
('d1e2f3a4-0001-4000-8000-000000000009', 'a1b2c3d4-0001-4000-8000-000000000002', 'c1d2e3f4-0001-4000-8000-000000000008', 800.00, 4200.00, 18.00, 900.00, 5900.00, 2, 'ASUS ROG display cable and panel replacement', 'PENDING');
