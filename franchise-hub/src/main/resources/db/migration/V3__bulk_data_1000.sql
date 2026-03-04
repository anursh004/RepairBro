-- V3__bulk_data_1000.sql — 1000 royalty records (3 branches × 36 months + extra)

-- ═══════ 1000 ROYALTY RECORDS ═══════
INSERT INTO royalty_record (franchise_id, branch_id, period, gross_revenue, royalty_percent, royalty_amount, payment_status, created_at)
SELECT
    (ARRAY[
        'd1e2f3a4-0001-4000-8000-000000000001',
        'd1e2f3a4-0001-4000-8000-000000000002',
        'd1e2f3a4-0001-4000-8000-000000000003'
    ]::uuid[])[1 + (s % 3)],
    (ARRAY[
        'a1b2c3d4-0001-4000-8000-000000000001',
        'a1b2c3d4-0001-4000-8000-000000000002',
        'a1b2c3d4-0001-4000-8000-000000000003'
    ]::uuid[])[1 + (s % 3)],
    to_char(CURRENT_DATE - ((s * 30) || ' days')::interval, 'YYYY-MM'),
    -- Gross revenue (tiered by branch)
    CASE
        WHEN s % 3 = 0 THEN round((200000 + random() * 150000)::numeric, 2)  -- Mumbai
        WHEN s % 3 = 1 THEN round((120000 + random() * 100000)::numeric, 2)  -- Pune
        ELSE round((70000 + random() * 60000)::numeric, 2)                    -- Nagpur
    END,
    7.00,
    -- Royalty = 7% of revenue
    CASE
        WHEN s % 3 = 0 THEN round(((200000 + random() * 150000) * 0.07)::numeric, 2)
        WHEN s % 3 = 1 THEN round(((120000 + random() * 100000) * 0.07)::numeric, 2)
        ELSE round(((70000 + random() * 60000) * 0.07)::numeric, 2)
    END,
    CASE WHEN s > 6 THEN 'PAID' ELSE (ARRAY['PAID','PENDING','PENDING'])[1 + (s % 3)] END,
    now() - ((s * 30)::text || ' days')::interval
FROM generate_series(1, 1000) AS s;
