-- V5__bulk_data_1000.sql — 1000 invoices + 1000 payments using generate_series()

-- ═══════ 1000 INVOICES ═══════
INSERT INTO invoice (id, invoice_number, ticket_id, branch_id, customer_id, subtotal, tax_amount, tax_rate, discount, total_amount, status, paid_at, created_at)
SELECT
    gen_random_uuid(),
    'INV-2024-' || lpad((2000 + s)::text, 5, '0'),
    -- Reference tickets from bulk-generated IDs (we use gen_random_uuid for ticket refs)
    gen_random_uuid(),
    CASE
        WHEN s % 10 < 5 THEN 'a1b2c3d4-0001-4000-8000-000000000001'::uuid
        WHEN s % 10 < 8 THEN 'a1b2c3d4-0001-4000-8000-000000000002'::uuid
        ELSE 'a1b2c3d4-0001-4000-8000-000000000003'::uuid
    END,
    (ARRAY[
        'c1d2e3f4-0001-4000-8000-000000000001','c1d2e3f4-0001-4000-8000-000000000002','c1d2e3f4-0001-4000-8000-000000000003',
        'c1d2e3f4-0001-4000-8000-000000000004','c1d2e3f4-0001-4000-8000-000000000005','c1d2e3f4-0001-4000-8000-000000000006',
        'c1d2e3f4-0001-4000-8000-000000000007','c1d2e3f4-0001-4000-8000-000000000008','c1d2e3f4-0001-4000-8000-000000000009',
        'c1d2e3f4-0001-4000-8000-000000000010','c1d2e3f4-0001-4000-8000-000000000011','c1d2e3f4-0001-4000-8000-000000000012',
        'c1d2e3f4-0001-4000-8000-000000000013','c1d2e3f4-0001-4000-8000-000000000014','c1d2e3f4-0001-4000-8000-000000000015'
    ]::uuid[])[1 + (s % 15)],
    -- Subtotal (antes impuestos)
    round((800 + (s * 47 % 9200))::numeric, 2),
    -- Tax amount (18% GST)
    round(((800 + (s * 47 % 9200)) * 0.18)::numeric, 2),
    18.00,
    -- Discount (10% of invoices get 5-10% discount)
    CASE WHEN s % 10 = 0 THEN round(((800 + (s * 47 % 9200)) * 0.05)::numeric, 2) ELSE 0 END,
    -- Total
    round(((800 + (s * 47 % 9200)) * 1.18 - CASE WHEN s % 10 = 0 THEN (800 + (s * 47 % 9200)) * 0.05 ELSE 0 END)::numeric, 2),
    -- Status distribution: 70% PAID, 15% SENT, 10% DRAFT, 5% CANCELLED
    (ARRAY['PAID','PAID','PAID','PAID','PAID','PAID','PAID','SENT','SENT','SENT','DRAFT','DRAFT','CANCELLED','PAID','PAID','PAID','PAID','PAID','PAID','PAID'])[1 + (s % 20)],
    -- paid_at for PAID invoices
    CASE WHEN s % 20 < 7 OR s % 20 >= 13 THEN now() - ((s % 90)::text || ' days')::interval ELSE NULL END,
    now() - ((s % 90 + 1)::text || ' days')::interval
FROM generate_series(1, 1000) AS s;

-- ═══════ 1000 PAYMENTS ═══════
INSERT INTO payment (invoice_id, amount, method, status, transaction_id, created_at)
SELECT
    inv.id,
    inv.total_amount,
    (ARRAY['UPI','CARD','CASH','UPI','CARD','UPI','CARD','UPI','NET_BANKING','UPI'])[1 + (row_number() OVER () % 10)::int],
    'COMPLETED',
    CASE
        WHEN row_number() OVER () % 3 = 0 THEN 'UPI-TXN-' || to_char(inv.created_at, 'YYYYMMDD') || '-' || lpad((row_number() OVER ())::text, 4, '0')
        WHEN row_number() OVER () % 3 = 1 THEN 'CARD-TXN-' || to_char(inv.created_at, 'YYYYMMDD') || '-' || lpad((row_number() OVER ())::text, 4, '0')
        ELSE 'CASH-REC-' || to_char(inv.created_at, 'YYYYMMDD') || '-' || lpad((row_number() OVER ())::text, 4, '0')
    END,
    inv.paid_at
FROM invoice inv
WHERE inv.status = 'PAID' AND inv.paid_at IS NOT NULL
LIMIT 1000;

-- ═══════ 1000 ESTIMATES ═══════
INSERT INTO estimate (ticket_id, branch_id, customer_id, labor_cost, parts_cost, tax_rate, tax_amount, total_estimate, estimated_days, work_description, status, created_at)
SELECT
    gen_random_uuid(),
    CASE
        WHEN s % 10 < 5 THEN 'a1b2c3d4-0001-4000-8000-000000000001'::uuid
        WHEN s % 10 < 8 THEN 'a1b2c3d4-0001-4000-8000-000000000002'::uuid
        ELSE 'a1b2c3d4-0001-4000-8000-000000000003'::uuid
    END,
    (ARRAY[
        'c1d2e3f4-0001-4000-8000-000000000001','c1d2e3f4-0001-4000-8000-000000000002','c1d2e3f4-0001-4000-8000-000000000003',
        'c1d2e3f4-0001-4000-8000-000000000004','c1d2e3f4-0001-4000-8000-000000000005','c1d2e3f4-0001-4000-8000-000000000006',
        'c1d2e3f4-0001-4000-8000-000000000007','c1d2e3f4-0001-4000-8000-000000000008','c1d2e3f4-0001-4000-8000-000000000009',
        'c1d2e3f4-0001-4000-8000-000000000010'
    ]::uuid[])[1 + (s % 10)],
    -- Labor cost ₹200-₹3000
    round((200 + (s * 29 % 2800))::numeric, 2),
    -- Parts cost ₹300-₹8000
    round((300 + (s * 43 % 7700))::numeric, 2),
    18.00,
    -- Tax on (labor+parts)
    round(((500 + (s * 29 % 2800) + (s * 43 % 7700)) * 0.18)::numeric, 2),
    -- Total
    round(((500 + (s * 29 % 2800) + (s * 43 % 7700)) * 1.18)::numeric, 2),
    1 + (s % 5),
    (ARRAY['Screen replacement with calibration','Battery replacement and system check','Keyboard replacement and driver update',
           'Board-level repair — component replacement','Water damage cleanup and restoration',
           'SSD upgrade with data migration','Fan and thermal paste replacement','Speaker module replacement',
           'Camera module replacement','USB port and charging port repair'])[1 + (s % 10)],
    (ARRAY['APPROVED','APPROVED','APPROVED','PENDING','PENDING','REJECTED','APPROVED','APPROVED','PENDING','APPROVED'])[1 + (s % 10)],
    now() - ((s % 60)::text || ' days')::interval
FROM generate_series(1, 1000) AS s;
