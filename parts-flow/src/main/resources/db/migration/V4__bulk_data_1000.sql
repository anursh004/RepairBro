-- V4__bulk_data_1000.sql — 1000 procurement orders + expanded inventory

-- ═══════ 1000 PROCUREMENT ORDERS ═══════
INSERT INTO procurement_order (order_number, branch_id, spare_part_id, quantity, total_cost, supplier_name, status, supplier_id, created_at)
SELECT
    'PO-2024-' || lpad((1000 + s)::text, 5, '0'),
    CASE
        WHEN s % 10 < 5 THEN 'a1b2c3d4-0001-4000-8000-000000000001'::uuid
        WHEN s % 10 < 8 THEN 'a1b2c3d4-0001-4000-8000-000000000002'::uuid
        ELSE 'a1b2c3d4-0001-4000-8000-000000000003'::uuid
    END,
    (ARRAY[
        'f1a2b3c4-0001-4000-8000-000000000001','f1a2b3c4-0001-4000-8000-000000000002','f1a2b3c4-0001-4000-8000-000000000003',
        'f1a2b3c4-0001-4000-8000-000000000004','f1a2b3c4-0001-4000-8000-000000000005','f1a2b3c4-0001-4000-8000-000000000006',
        'f1a2b3c4-0001-4000-8000-000000000007','f1a2b3c4-0001-4000-8000-000000000008','f1a2b3c4-0001-4000-8000-000000000009',
        'f1a2b3c4-0001-4000-8000-000000000010','f1a2b3c4-0001-4000-8000-000000000011','f1a2b3c4-0001-4000-8000-000000000012',
        'f1a2b3c4-0001-4000-8000-000000000013','f1a2b3c4-0001-4000-8000-000000000014','f1a2b3c4-0001-4000-8000-000000000015',
        'f1a2b3c4-0001-4000-8000-000000000016','f1a2b3c4-0001-4000-8000-000000000017','f1a2b3c4-0001-4000-8000-000000000018',
        'f1a2b3c4-0001-4000-8000-000000000019','f1a2b3c4-0001-4000-8000-000000000020'
    ]::uuid[])[1 + (s % 20)],
    -- Quantity: 2-25 units
    2 + (s * 3 % 23),
    -- Total cost
    round(((2 + (s * 3 % 23)) * (300 + (s * 41 % 4700)))::numeric, 2),
    (ARRAY['MobileParts India','LaptopSpare Pro','TechParts Wholesale'])[1 + (s % 3)],
    -- Status: 60% DELIVERED, 20% PENDING, 15% IN_TRANSIT, 5% CANCELLED
    (ARRAY['DELIVERED','DELIVERED','DELIVERED','DELIVERED','DELIVERED','DELIVERED',
           'DELIVERED','DELIVERED','DELIVERED','DELIVERED','DELIVERED','DELIVERED',
           'PENDING','PENDING','PENDING','PENDING',
           'IN_TRANSIT','IN_TRANSIT','IN_TRANSIT',
           'CANCELLED'])[1 + (s % 20)],
    (ARRAY[
        'e1f2a3b4-0001-4000-8000-000000000001',
        'e1f2a3b4-0001-4000-8000-000000000002',
        'e1f2a3b4-0001-4000-8000-000000000003'
    ]::uuid[])[1 + (s % 3)],
    now() - ((s % 180)::text || ' days')::interval
FROM generate_series(1, 1000) AS s;
