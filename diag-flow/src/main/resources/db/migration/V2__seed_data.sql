-- V2__seed_data.sql — Seed data for diag-flow

-- ═══════ DIAGNOSTIC FLOWS ═══════
INSERT INTO diag_flow (id, name, device_type, symptom_category, description, version) VALUES
('b1c2d3e4-0001-4000-8000-000000000001', 'Laptop No Power Diagnosis', 'LAPTOP', 'power', 'Step-by-step diagnosis for laptops that won''t power on', 1),
('b1c2d3e4-0001-4000-8000-000000000002', 'Screen/Display Issues', 'LAPTOP', 'display', 'Diagnosis for flickering, blank, or cracked displays', 1),
('b1c2d3e4-0001-4000-8000-000000000003', 'Battery Health Check', 'LAPTOP', 'battery', 'Battery drain, swelling, and charge issues', 1),
('b1c2d3e4-0001-4000-8000-000000000004', 'Water Damage Assessment', 'LAPTOP', 'water_damage', 'Liquid spill triage and corrosion check', 1),
('b1c2d3e4-0001-4000-8000-000000000005', 'Mobile Screen Repair', 'MOBILE', 'display', 'Cracked/broken screen diagnosis for phones', 1),
('b1c2d3e4-0001-4000-8000-000000000006', 'Mobile Audio Issues', 'MOBILE', 'audio', 'Speaker, microphone, and earpiece diagnosis', 1),
('b1c2d3e4-0001-4000-8000-000000000007', 'Desktop BSOD/Crash', 'DESKTOP', 'crash', 'Blue screen, random reboots, and instability', 1),
('b1c2d3e4-0001-4000-8000-000000000008', 'Overheating Diagnosis', 'LAPTOP', 'thermal', 'Fan noise, thermal throttling, and shutdown issues', 1);

-- ═══════ FLOW STEPS ═══════
-- Laptop No Power (6 steps)
INSERT INTO diag_flow_step (diag_flow_id, step_order, instruction, expected_outcome, confidence_weight) VALUES
('b1c2d3e4-0001-4000-8000-000000000001', 1, 'Check power adapter output with multimeter (19V DC)', 'PASS/FAIL', 0.15),
('b1c2d3e4-0001-4000-8000-000000000001', 2, 'Inspect battery health and voltage', 'PASS/FAIL', 0.15),
('b1c2d3e4-0001-4000-8000-000000000001', 3, 'Test DC jack / charging port continuity', 'PASS/FAIL', 0.10),
('b1c2d3e4-0001-4000-8000-000000000001', 4, 'Remove and reseat RAM modules', 'PASS/FAIL', 0.10),
('b1c2d3e4-0001-4000-8000-000000000001', 5, 'Check motherboard for visible damage', 'PASS/FAIL', 0.20),
('b1c2d3e4-0001-4000-8000-000000000001', 6, 'Measure standby voltages (3.3V, 5V, 12V)', 'PASS/FAIL', 0.30),
-- Screen Issues (5 steps)
('b1c2d3e4-0001-4000-8000-000000000002', 1, 'Visual inspection for cracks and pressure marks', 'DAMAGED/INTACT', 0.20),
('b1c2d3e4-0001-4000-8000-000000000002', 2, 'Connect external monitor via HDMI', 'DISPLAY_OK/NO_SIGNAL', 0.25),
('b1c2d3e4-0001-4000-8000-000000000002', 3, 'Inspect display cable for damage', 'PASS/FAIL', 0.15),
('b1c2d3e4-0001-4000-8000-000000000002', 4, 'Test with replacement panel if available', 'PASS/FAIL', 0.25),
('b1c2d3e4-0001-4000-8000-000000000002', 5, 'Check backlight fuse and inverter', 'PASS/FAIL', 0.15),
-- Battery Health (4 steps)
('b1c2d3e4-0001-4000-8000-000000000003', 1, 'Check battery health via OS diagnostic', 'CYCLE_COUNT/WEAR_LEVEL', 0.25),
('b1c2d3e4-0001-4000-8000-000000000003', 2, 'Visual inspection for swelling', 'NORMAL/SWOLLEN', 0.25),
('b1c2d3e4-0001-4000-8000-000000000003', 3, 'Measure battery voltage with multimeter', 'PASS/FAIL', 0.20),
('b1c2d3e4-0001-4000-8000-000000000003', 4, 'Test with replacement battery', 'PASS/FAIL', 0.30),
-- Water Damage (5 steps)
('b1c2d3e4-0001-4000-8000-000000000004', 1, 'IMMEDIATE: Power off and remove battery', 'DONE', 0.10),
('b1c2d3e4-0001-4000-8000-000000000004', 2, 'Disassemble and inspect for corrosion under magnification', 'CORROSION_FOUND/CLEAN', 0.20),
('b1c2d3e4-0001-4000-8000-000000000004', 3, 'Clean with 99% isopropyl alcohol', 'CLEANED', 0.15),
('b1c2d3e4-0001-4000-8000-000000000004', 4, 'Check liquid contact indicators (LCIs)', 'TRIGGERED/CLEAR', 0.15),
('b1c2d3e4-0001-4000-8000-000000000004', 5, 'Reassemble after 24h and test all functions', 'PASS/PARTIAL/FAIL', 0.40);

-- ═══════ DIAGNOSIS EVALUATIONS (for active tickets) ═══════
INSERT INTO diagnosis_evaluation (ticket_id, diag_flow_id, confidence_score, suggested_actions, root_cause, status) VALUES
('d1e2f3a4-0001-4000-8000-000000000004', 'b1c2d3e4-0001-4000-8000-000000000007', 0.65, 'Step 1: Run MemTest86\nStep 2: Check CPU temperatures\nStep 3: Reseat RAM and GPU', 'Likely cause aligned with: Desktop BSOD/Crash Diagnosis', 'IN_PROGRESS'),
('d1e2f3a4-0001-4000-8000-000000000013', NULL, 0.40, 'Step 1: Check Face ID sensor alignment\nStep 2: Inspect front camera flex cable\nStep 3: Test with known-good sensor module', 'Manual diagnosis required — specialized Apple part', 'PENDING');
