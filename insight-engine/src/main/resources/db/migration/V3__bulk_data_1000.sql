-- V3__bulk_data_1000.sql — 365 days × 3 branches = 1095 KPI entries + 1000 procurement orders (parts-flow)

-- ═══════ 1095 BRANCH KPI ENTRIES (1 year of daily data × 3 branches) ═══════
INSERT INTO branch_kpi_daily (branch_id, date, tickets_created, tickets_completed, sl_breaches, revenue, mttr_hours, ftfr, tech_utilization)
SELECT
    branch_uuid,
    (CURRENT_DATE - d)::date,
    -- Tickets created (varies by tier)
    CASE
        WHEN branch_uuid = 'a1b2c3d4-0001-4000-8000-000000000001'::uuid THEN 5 + (random() * 10)::int  -- Mumbai: 5-15
        WHEN branch_uuid = 'a1b2c3d4-0001-4000-8000-000000000002'::uuid THEN 3 + (random() * 7)::int   -- Pune: 3-10
        ELSE 2 + (random() * 5)::int                                                                     -- Nagpur: 2-7
    END,
    -- Tickets completed (80-95% of created)
    CASE
        WHEN branch_uuid = 'a1b2c3d4-0001-4000-8000-000000000001'::uuid THEN 4 + (random() * 9)::int
        WHEN branch_uuid = 'a1b2c3d4-0001-4000-8000-000000000002'::uuid THEN 2 + (random() * 7)::int
        ELSE 1 + (random() * 5)::int
    END,
    -- SLA breaches (5-10% chance)
    CASE WHEN random() < 0.08 THEN 1 + (random() * 2)::int ELSE 0 END,
    -- Revenue
    CASE
        WHEN branch_uuid = 'a1b2c3d4-0001-4000-8000-000000000001'::uuid THEN round((8000 + random() * 20000)::numeric, 2)
        WHEN branch_uuid = 'a1b2c3d4-0001-4000-8000-000000000002'::uuid THEN round((5000 + random() * 12000)::numeric, 2)
        ELSE round((2000 + random() * 8000)::numeric, 2)
    END,
    -- MTTR hours
    round((1.5 + random() * 3.5)::numeric, 1),
    -- FTFR
    round((0.65 + random() * 0.30)::numeric, 2),
    -- Tech utilization
    round((0.30 + random() * 0.60)::numeric, 2)
FROM
    generate_series(1, 365) AS d,
    unnest(ARRAY[
        'a1b2c3d4-0001-4000-8000-000000000001'::uuid,
        'a1b2c3d4-0001-4000-8000-000000000002'::uuid,
        'a1b2c3d4-0001-4000-8000-000000000003'::uuid
    ]) AS branch_uuid;
