-- V4__bulk_data_1000.sql — 1000 SLA records + 1000 warranties

-- ═══════ 1000 SLA RECORDS ═══════
INSERT INTO sla_record (id, ticket_id, branch_id, sla_type, deadline, status, resolved_at, created_at)
SELECT
    gen_random_uuid(),
    gen_random_uuid(),
    CASE
        WHEN s % 10 < 5 THEN 'a1b2c3d4-0001-4000-8000-000000000001'::uuid
        WHEN s % 10 < 8 THEN 'a1b2c3d4-0001-4000-8000-000000000002'::uuid
        ELSE 'a1b2c3d4-0001-4000-8000-000000000003'::uuid
    END,
    CASE WHEN s % 5 = 0 THEN 'URGENT_24H' ELSE 'STANDARD_48H' END,
    now() - ((s % 60 - 30)::text || ' days')::interval,
    -- Status: 70% RESOLVED, 15% ACTIVE, 15% BREACHED
    (ARRAY['RESOLVED','RESOLVED','RESOLVED','RESOLVED','RESOLVED','RESOLVED','RESOLVED',
           'ACTIVE','ACTIVE','ACTIVE','BREACHED','BREACHED','BREACHED',
           'RESOLVED','RESOLVED','RESOLVED','RESOLVED','RESOLVED','RESOLVED','RESOLVED'])[1 + (s % 20)],
    CASE WHEN s % 20 < 7 OR s % 20 >= 13
         THEN now() - ((s % 60 - 28)::text || ' days')::interval
         ELSE NULL END,
    now() - ((s % 60 + 1)::text || ' days')::interval
FROM generate_series(1, 1000) AS s;

-- ═══════ 1000 WARRANTIES ═══════
INSERT INTO warranty (ticket_id, customer_id, branch_id, category, warranty_days, start_date, expiry_date, status, description, created_at)
SELECT
    gen_random_uuid(),
    (ARRAY[
        'c1d2e3f4-0001-4000-8000-000000000001','c1d2e3f4-0001-4000-8000-000000000002','c1d2e3f4-0001-4000-8000-000000000003',
        'c1d2e3f4-0001-4000-8000-000000000004','c1d2e3f4-0001-4000-8000-000000000005','c1d2e3f4-0001-4000-8000-000000000006',
        'c1d2e3f4-0001-4000-8000-000000000007','c1d2e3f4-0001-4000-8000-000000000008','c1d2e3f4-0001-4000-8000-000000000009',
        'c1d2e3f4-0001-4000-8000-000000000010','c1d2e3f4-0001-4000-8000-000000000011','c1d2e3f4-0001-4000-8000-000000000012',
        'c1d2e3f4-0001-4000-8000-000000000013','c1d2e3f4-0001-4000-8000-000000000014','c1d2e3f4-0001-4000-8000-000000000015'
    ]::uuid[])[1 + (s % 15)],
    CASE
        WHEN s % 10 < 5 THEN 'a1b2c3d4-0001-4000-8000-000000000001'::uuid
        WHEN s % 10 < 8 THEN 'a1b2c3d4-0001-4000-8000-000000000002'::uuid
        ELSE 'a1b2c3d4-0001-4000-8000-000000000003'::uuid
    END,
    (ARRAY['QUICK_FIX','STANDARD','STANDARD','ADVANCED','STANDARD'])[1 + (s % 5)],
    (ARRAY[30, 60, 60, 90, 60])[1 + (s % 5)],
    (now() - ((s % 120)::text || ' days')::interval)::date,
    (now() - ((s % 120)::text || ' days')::interval + ((ARRAY[30,60,60,90,60])[1 + (s % 5)]::text || ' days')::interval)::date,
    -- 60% ACTIVE, 25% EXPIRED, 15% CLAIMED
    (ARRAY['ACTIVE','ACTIVE','ACTIVE','ACTIVE','ACTIVE','ACTIVE',
           'ACTIVE','ACTIVE','ACTIVE','ACTIVE','ACTIVE','ACTIVE',
           'EXPIRED','EXPIRED','EXPIRED','EXPIRED','EXPIRED',
           'CLAIMED','CLAIMED','CLAIMED'])[1 + (s % 20)],
    (ARRAY['Screen replacement warranty','Battery replacement warranty','Keyboard repair warranty',
           'Board-level repair warranty','Charging port warranty','Speaker module warranty',
           'SSD replacement warranty','Camera module warranty','Fan replacement warranty','General repair warranty'])[1 + (s % 10)],
    now() - ((s % 120)::text || ' days')::interval
FROM generate_series(1, 1000) AS s;

-- ═══════ 1000 COMPLAINTS ═══════
INSERT INTO complaint (ticket_id, customer_id, branch_id, description, status, resolution, created_at)
SELECT
    gen_random_uuid(),
    (ARRAY[
        'c1d2e3f4-0001-4000-8000-000000000001','c1d2e3f4-0001-4000-8000-000000000002','c1d2e3f4-0001-4000-8000-000000000003',
        'c1d2e3f4-0001-4000-8000-000000000004','c1d2e3f4-0001-4000-8000-000000000005','c1d2e3f4-0001-4000-8000-000000000006',
        'c1d2e3f4-0001-4000-8000-000000000007','c1d2e3f4-0001-4000-8000-000000000008','c1d2e3f4-0001-4000-8000-000000000009',
        'c1d2e3f4-0001-4000-8000-000000000010'
    ]::uuid[])[1 + (s % 10)],
    CASE
        WHEN s % 10 < 5 THEN 'a1b2c3d4-0001-4000-8000-000000000001'::uuid
        WHEN s % 10 < 8 THEN 'a1b2c3d4-0001-4000-8000-000000000002'::uuid
        ELSE 'a1b2c3d4-0001-4000-8000-000000000003'::uuid
    END,
    (ARRAY['Repair took longer than promised','Device returned with new issue','Poor communication about status',
           'Overcharged compared to estimate','Parts quality is substandard','Technician was unprofessional',
           'Same issue recurred after repair','Not informed about delays','Wrong diagnosis initially',
           'Warranty claim was rejected unfairly'])[1 + (s % 10)],
    (ARRAY['OPEN','OPEN','RESOLVED','RESOLVED','RESOLVED','OPEN','RESOLVED','RESOLVED','OPEN','RESOLVED'])[1 + (s % 10)],
    CASE WHEN s % 10 IN (2,3,4,6,7,9)
         THEN 'Issue was investigated and resolved. Customer was compensated appropriately.'
         ELSE NULL END,
    now() - ((s % 90)::text || ' days')::interval
FROM generate_series(1, 1000) AS s;
