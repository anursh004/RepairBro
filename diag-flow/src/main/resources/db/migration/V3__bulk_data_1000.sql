-- V3__bulk_data_1000.sql — 1000 diagnostic evaluations for diag-flow

INSERT INTO diagnosis_evaluation (ticket_id, diag_flow_id, confidence_score, suggested_actions, root_cause, status, created_at)
SELECT
    gen_random_uuid(),
    (ARRAY[
        'b1c2d3e4-0001-4000-8000-000000000001','b1c2d3e4-0001-4000-8000-000000000002',
        'b1c2d3e4-0001-4000-8000-000000000003','b1c2d3e4-0001-4000-8000-000000000004',
        'b1c2d3e4-0001-4000-8000-000000000005','b1c2d3e4-0001-4000-8000-000000000006',
        'b1c2d3e4-0001-4000-8000-000000000007','b1c2d3e4-0001-4000-8000-000000000008'
    ]::uuid[])[1 + (s % 8)],
    -- Confidence: 0.40 - 0.98
    round((0.40 + (s * 7 % 58) / 100.0)::numeric, 2),
    -- Suggested actions
    (ARRAY[
        'Step 1: Check power adapter output\nStep 2: Test battery voltage\nStep 3: Inspect DC board',
        'Step 1: Visual inspection for cracks\nStep 2: Connect external display\nStep 3: Check LVDS cable',
        'Step 1: Run battery health check\nStep 2: Measure voltage\nStep 3: Test with replacement battery',
        'Step 1: Power off immediately\nStep 2: Disassemble and inspect for corrosion\nStep 3: Clean with IPA',
        'Step 1: Test touch zones\nStep 2: Inspect digitizer cable\nStep 3: Replace screen assembly',
        'Step 1: Test speaker output\nStep 2: Check audio IC\nStep 3: Replace speaker module',
        'Step 1: Run MemTest86\nStep 2: Check CPU temperatures\nStep 3: Stress test GPU',
        'Step 1: Clean fan and heatsink\nStep 2: Replace thermal paste\nStep 3: Test under load'
    ])[1 + (s % 8)],
    (ARRAY[
        'Power delivery failure — DC board or battery fault',
        'Display hardware failure — panel or cable damage',
        'Battery degradation — cycle count exceeded or cell failure',
        'Liquid damage — corrosion on main board',
        'Digitizer failure — cracked or delaminated touch panel',
        'Audio IC failure or speaker module damage',
        'Memory or GPU instability — possible hardware failure',
        'Thermal system failure — fan or heatsink blocked'
    ])[1 + (s % 8)],
    (ARRAY['COMPLETED','COMPLETED','COMPLETED','IN_PROGRESS','COMPLETED','COMPLETED','IN_PROGRESS','COMPLETED','PENDING','COMPLETED'])[1 + (s % 10)],
    now() - ((s % 120)::text || ' days')::interval
FROM generate_series(1, 1000) AS s;
