-- V3__seed_data.sql — Production-ready seed data for repair-core

-- ═══════ BRANCHES ═══════
INSERT INTO branch (id, name, city, tier, address, phone, email, monthly_rent) VALUES
('a1b2c3d4-0001-4000-8000-000000000001', 'RepairBro Mumbai Central', 'Mumbai', 1, '45 Linking Road, Bandra West, Mumbai 400050', '022-26401234', 'mumbai@repairbro.in', 120000.00),
('a1b2c3d4-0001-4000-8000-000000000002', 'RepairBro Pune Camp', 'Pune', 2, '12 MG Road, Camp, Pune 411001', '020-26131234', 'pune@repairbro.in', 55000.00),
('a1b2c3d4-0001-4000-8000-000000000003', 'RepairBro Nagpur Sadar', 'Nagpur', 3, '78 Central Bazaar, Sadar, Nagpur 440001', '0712-2561234', 'nagpur@repairbro.in', 30000.00);

-- ═══════ CUSTOMERS ═══════
INSERT INTO customer (id, name, phone, email, address, city, pincode, preferred_channel, total_repairs) VALUES
('c1d2e3f4-0001-4000-8000-000000000001', 'Rajesh Sharma', '9876543210', 'rajesh.sharma@gmail.com', '301 Sunrise Apts, Andheri East', 'Mumbai', '400069', 'SMS', 3),
('c1d2e3f4-0001-4000-8000-000000000002', 'Priya Deshmukh', '9876543211', 'priya.d@yahoo.com', '15 Koregaon Park', 'Pune', '411001', 'EMAIL', 1),
('c1d2e3f4-0001-4000-8000-000000000003', 'Amit Patel', '9876543212', 'amit.patel@outlook.com', '42 Civil Lines', 'Nagpur', '440001', 'WHATSAPP', 2),
('c1d2e3f4-0001-4000-8000-000000000004', 'Sneha Iyer', '9876543213', 'sneha.iyer@gmail.com', '88 Dadar TT Circle', 'Mumbai', '400014', 'SMS', 0),
('c1d2e3f4-0001-4000-8000-000000000005', 'Vikram Joshi', '9876543214', 'vikram.j@hotmail.com', '23 Viman Nagar', 'Pune', '411014', 'EMAIL', 5),
('c1d2e3f4-0001-4000-8000-000000000006', 'Deepa Nair', '9876543215', 'deepa.nair@gmail.com', '67 Juhu Beach Road', 'Mumbai', '400049', 'SMS', 1),
('c1d2e3f4-0001-4000-8000-000000000007', 'Suresh Gupta', '9876543216', 'suresh.gupta@gmail.com', '9 Dharampeth', 'Nagpur', '440010', 'WHATSAPP', 0),
('c1d2e3f4-0001-4000-8000-000000000008', 'Anita Kulkarni', '9876543217', 'anita.k@gmail.com', '34 FC Road', 'Pune', '411004', 'EMAIL', 2),
('c1d2e3f4-0001-4000-8000-000000000009', 'Rohit Mehta', '9876543218', 'rohit.m@outlook.com', '56 Powai Lake Road', 'Mumbai', '400076', 'SMS', 1),
('c1d2e3f4-0001-4000-8000-000000000010', 'Kavita Singh', '9876543219', 'kavita.s@gmail.com', '108 Sitabuldi', 'Nagpur', '440012', 'WHATSAPP', 0),
('c1d2e3f4-0001-4000-8000-000000000011', 'Manish Agarwal', '9876543220', 'manish.a@gmail.com', '21 Bandra Kurla Complex', 'Mumbai', '400051', 'SMS', 4),
('c1d2e3f4-0001-4000-8000-000000000012', 'Pooja Thakur', '9876543221', 'pooja.t@yahoo.com', '5 Aundh', 'Pune', '411007', 'EMAIL', 0),
('c1d2e3f4-0001-4000-8000-000000000013', 'Nikhil Verma', '9876543222', 'nikhil.v@gmail.com', '89 Lower Parel', 'Mumbai', '400013', 'SMS', 2),
('c1d2e3f4-0001-4000-8000-000000000014', 'Swati Desai', '9876543223', 'swati.d@outlook.com', '41 Shivajinagar', 'Pune', '411005', 'EMAIL', 1),
('c1d2e3f4-0001-4000-8000-000000000015', 'Arun Kumar', '9876543224', 'arun.k@gmail.com', '12 Wardha Road', 'Nagpur', '440015', 'WHATSAPP', 0);

-- ═══════ REPAIR TICKETS ═══════
-- Mumbai branch tickets
INSERT INTO repair_ticket (id, branch_id, customer_id, assigned_tech_id, device_type, device_model, device_serial, symptom, status, priority, estimated_cost, final_cost, notes) VALUES
('d1e2f3a4-0001-4000-8000-000000000001', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000001', 'b1c2d3e4-0001-4000-8000-000000000001', 'LAPTOP', 'MacBook Air M2', 'C02X12345678', 'Not powering on, no LED activity', 'COMPLETED', 'NORMAL', 3500.00, 3200.00, 'Replaced DC board connector'),
('d1e2f3a4-0001-4000-8000-000000000002', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000004', NULL, 'MOBILE', 'iPhone 15 Pro', 'DNXYZ1234567', 'Cracked screen after drop', 'OPEN', 'NORMAL', 8500.00, NULL, NULL),
('d1e2f3a4-0001-4000-8000-000000000003', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000006', 'b1c2d3e4-0001-4000-8000-000000000002', 'LAPTOP', 'Dell XPS 15', 'DLL98765432', 'Battery swollen, trackpad bulging', 'IN_REPAIR', 'URGENT', 4200.00, NULL, 'Battery replaced, testing'),
('d1e2f3a4-0001-4000-8000-000000000004', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000009', 'b1c2d3e4-0001-4000-8000-000000000001', 'DESKTOP', 'Custom Build i7', 'CSTM20240001', 'Blue screen crashes, random reboots', 'DIAGNOSING', 'NORMAL', 2000.00, NULL, NULL),
('d1e2f3a4-0001-4000-8000-000000000005', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000011', NULL, 'MOBILE', 'Samsung Galaxy S24', 'RF8R30ABCDE', 'Water spill, phone not responding', 'WAITING_FOR_PARTS', 'URGENT', 6000.00, NULL, 'Needs replacement board'),
('d1e2f3a4-0001-4000-8000-000000000006', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000013', 'b1c2d3e4-0001-4000-8000-000000000001', 'LAPTOP', 'Lenovo ThinkPad X1', 'MPXYZ9876543', 'Keyboard keys not registering', 'QA_CHECK', 'NORMAL', 1800.00, 1650.00, 'Keyboard replaced, running final tests'),
-- Pune branch tickets
('d1e2f3a4-0001-4000-8000-000000000007', 'a1b2c3d4-0001-4000-8000-000000000002', 'c1d2e3f4-0001-4000-8000-000000000002', 'b1c2d3e4-0001-4000-8000-000000000003', 'LAPTOP', 'HP Pavilion 14', 'HPXYZ5678901', 'Overheating during gaming', 'IN_REPAIR', 'NORMAL', 1200.00, NULL, 'Cleaning thermal paste and fan'),
('d1e2f3a4-0001-4000-8000-000000000008', 'a1b2c3d4-0001-4000-8000-000000000002', 'c1d2e3f4-0001-4000-8000-000000000005', 'b1c2d3e4-0001-4000-8000-000000000004', 'MOBILE', 'OnePlus 12', 'OP12345ABCDE', 'Speaker not working', 'COMPLETED', 'NORMAL', 900.00, 850.00, 'Replaced speaker module'),
('d1e2f3a4-0001-4000-8000-000000000009', 'a1b2c3d4-0001-4000-8000-000000000002', 'c1d2e3f4-0001-4000-8000-000000000008', NULL, 'LAPTOP', 'ASUS ROG Strix', 'ASRG98765432', 'Display flickering at high refresh rate', 'OPEN', 'NORMAL', 5500.00, NULL, NULL),
('d1e2f3a4-0001-4000-8000-000000000010', 'a1b2c3d4-0001-4000-8000-000000000002', 'c1d2e3f4-0001-4000-8000-000000000014', 'b1c2d3e4-0001-4000-8000-000000000003', 'DESKTOP', 'Lenovo IdeaCentre', 'LEN56789ABCD', 'Not booting, beep codes', 'READY_FOR_PICKUP', 'NORMAL', 1500.00, 1350.00, 'Replaced faulty RAM module'),
-- Nagpur branch tickets
('d1e2f3a4-0001-4000-8000-000000000011', 'a1b2c3d4-0001-4000-8000-000000000003', 'c1d2e3f4-0001-4000-8000-000000000003', 'b1c2d3e4-0001-4000-8000-000000000005', 'MOBILE', 'Xiaomi 14', 'XI14567890AB', 'Battery draining in 3 hours', 'COMPLETED', 'NORMAL', 1100.00, 1050.00, 'Battery replacement done'),
('d1e2f3a4-0001-4000-8000-000000000012', 'a1b2c3d4-0001-4000-8000-000000000003', 'c1d2e3f4-0001-4000-8000-000000000007', NULL, 'LAPTOP', 'Acer Aspire 5', 'ACR12345ABCD', 'Charging port loose', 'OPEN', 'NORMAL', 800.00, NULL, NULL),
('d1e2f3a4-0001-4000-8000-000000000013', 'a1b2c3d4-0001-4000-8000-000000000003', 'c1d2e3f4-0001-4000-8000-000000000010', 'b1c2d3e4-0001-4000-8000-000000000006', 'MOBILE', 'iPhone 14', 'FXYZ12345678', 'Face ID not working', 'DIAGNOSING', 'NORMAL', 4500.00, NULL, NULL),
('d1e2f3a4-0001-4000-8000-000000000014', 'a1b2c3d4-0001-4000-8000-000000000003', 'c1d2e3f4-0001-4000-8000-000000000015', 'b1c2d3e4-0001-4000-8000-000000000005', 'DESKTOP', 'HP ProDesk', 'HPPD98765ABC', 'Hard drive clicking noises', 'IN_REPAIR', 'URGENT', 3000.00, NULL, 'Cloning HDD to SSD');

-- ═══════ DIAGNOSIS STEPS (for completed/in-progress tickets) ═══════
INSERT INTO diagnosis_step (ticket_id, step_order, name, result, notes) VALUES
('d1e2f3a4-0001-4000-8000-000000000001', 1, 'Power adapter test', 'PASS', 'Adapter output 19.5V OK'),
('d1e2f3a4-0001-4000-8000-000000000001', 2, 'Battery voltage check', 'PASS', 'Battery at 11.8V'),
('d1e2f3a4-0001-4000-8000-000000000001', 3, 'DC board inspection', 'FAIL', 'Corroded connector pins'),
('d1e2f3a4-0001-4000-8000-000000000001', 4, 'Board replacement and test', 'PASS', 'New DC board installed, powers on'),
('d1e2f3a4-0001-4000-8000-000000000003', 1, 'Visual inspection', 'FAIL', 'Battery visibly swollen, trackpad displaced'),
('d1e2f3a4-0001-4000-8000-000000000003', 2, 'Battery removal', 'DONE', 'Old battery safely removed and disposed'),
('d1e2f3a4-0001-4000-8000-000000000004', 1, 'MemTest86 run', 'RUNNING', 'Testing RAM for errors');

-- ═══════ TICKET TIMELINE ═══════
INSERT INTO ticket_timeline (ticket_id, action, detail) VALUES
('d1e2f3a4-0001-4000-8000-000000000001', 'CREATED', 'Ticket created at intake'),
('d1e2f3a4-0001-4000-8000-000000000001', 'ASSIGNED', 'Assigned to Tech Arjun'),
('d1e2f3a4-0001-4000-8000-000000000001', 'STATUS_CHANGED', 'OPEN → DIAGNOSING'),
('d1e2f3a4-0001-4000-8000-000000000001', 'STATUS_CHANGED', 'DIAGNOSING → IN_REPAIR'),
('d1e2f3a4-0001-4000-8000-000000000001', 'STATUS_CHANGED', 'IN_REPAIR → QA_CHECK'),
('d1e2f3a4-0001-4000-8000-000000000001', 'STATUS_CHANGED', 'QA_CHECK → COMPLETED');
