-- V4__expanded_seed_data.sql — Additional realistic data for repair-core

-- ═══════ MORE CUSTOMERS (16-30) ═══════
INSERT INTO customer (id, name, phone, email, address, city, pincode, preferred_channel, total_repairs) VALUES
('c1d2e3f4-0001-4000-8000-000000000016', 'Ravi Krishnamurthy', '9876543225', 'ravi.krishna@gmail.com', '14 Koramangala 4th Block', 'Bangalore', '560034', 'EMAIL', 3),
('c1d2e3f4-0001-4000-8000-000000000017', 'Meera Rajan', '9876543226', 'meera.rajan@yahoo.com', '78 T Nagar', 'Chennai', '600017', 'SMS', 1),
('c1d2e3f4-0001-4000-8000-000000000018', 'Farhan Sheikh', '9876543227', 'farhan.sheikh@outlook.com', '23 Banjara Hills', 'Hyderabad', '500034', 'WHATSAPP', 2),
('c1d2e3f4-0001-4000-8000-000000000019', 'Divya Rao', '9876543228', 'divya.rao@gmail.com', '45 Salt Lake Sector V', 'Kolkata', '700091', 'EMAIL', 0),
('c1d2e3f4-0001-4000-8000-000000000020', 'Samir Jain', '9876543229', 'samir.jain@hotmail.com', '12 CG Road', 'Ahmedabad', '380006', 'SMS', 4),
('c1d2e3f4-0001-4000-8000-000000000021', 'Lakshmi Venkatesh', '9876543230', 'lakshmi.v@gmail.com', '67 Whitefield', 'Bangalore', '560066', 'EMAIL', 1),
('c1d2e3f4-0001-4000-8000-000000000022', 'Arjun Malhotra', '9876543231', 'arjun.malhotra@gmail.com', '34 Khan Market', 'New Delhi', '110003', 'SMS', 6),
('c1d2e3f4-0001-4000-8000-000000000023', 'Nidhi Saxena', '9876543232', 'nidhi.s@yahoo.com', '89 MG Road', 'Indore', '452001', 'WHATSAPP', 0),
('c1d2e3f4-0001-4000-8000-000000000024', 'Tanmay Bhat', '9876543233', 'tanmay.b@gmail.com', '56 Hill Road Bandra', 'Mumbai', '400050', 'SMS', 2),
('c1d2e3f4-0001-4000-8000-000000000025', 'Shweta Mishra', '9876543234', 'shweta.m@outlook.com', '12 Gomti Nagar', 'Lucknow', '226010', 'EMAIL', 1),
('c1d2e3f4-0001-4000-8000-000000000026', 'Karthik Raman', '9876543235', 'karthik.r@gmail.com', '34 Anna Nagar', 'Chennai', '600040', 'SMS', 3),
('c1d2e3f4-0001-4000-8000-000000000027', 'Prerna Goyal', '9876543236', 'prerna.g@yahoo.com', '78 Sector 14', 'Gurgaon', '122001', 'EMAIL', 0),
('c1d2e3f4-0001-4000-8000-000000000028', 'Harsh Vardhan', '9876543237', 'harsh.v@gmail.com', '23 Jubilee Hills', 'Hyderabad', '500033', 'WHATSAPP', 2),
('c1d2e3f4-0001-4000-8000-000000000029', 'Ananya Pillai', '9876543238', 'ananya.p@gmail.com', '45 Marine Drive', 'Kochi', '682031', 'EMAIL', 1),
('c1d2e3f4-0001-4000-8000-000000000030', 'Vivek Chauhan', '9876543239', 'vivek.chauhan@outlook.com', '67 Vaishali Nagar', 'Jaipur', '302021', 'SMS', 0);

-- ═══════ MORE TICKETS (15-28) ═══════
-- Mix of completed, in-progress, and open tickets for realism
INSERT INTO repair_ticket (id, branch_id, customer_id, assigned_tech_id, device_type, device_model, device_serial, symptom, status, priority, estimated_cost, final_cost, notes) VALUES
-- Completed tickets (for more billing/analytics data)
('d1e2f3a4-0001-4000-8000-000000000015', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000024', 'b1c2d3e4-0001-4000-8000-000000000002', 'MOBILE', 'Samsung Galaxy S23', 'RF8R30FGHIJ', 'Touch screen unresponsive in bottom area', 'COMPLETED', 'NORMAL', 3200.00, 2900.00, 'Digitizer replaced, fully functional'),
('d1e2f3a4-0001-4000-8000-000000000016', 'a1b2c3d4-0001-4000-8000-000000000002', 'c1d2e3f4-0001-4000-8000-000000000020', 'b1c2d3e4-0001-4000-8000-000000000004', 'LAPTOP', 'ASUS Vivobook 15', 'ASVI12345678', 'Keyboard backlight not working, several keys stuck', 'COMPLETED', 'NORMAL', 1800.00, 1650.00, 'Keyboard replaced, driver updated'),
('d1e2f3a4-0001-4000-8000-000000000017', 'a1b2c3d4-0001-4000-8000-000000000003', 'c1d2e3f4-0001-4000-8000-000000000023', 'b1c2d3e4-0001-4000-8000-000000000006', 'MOBILE', 'iPhone 13', 'FXYZ87654321', 'Front camera blurry, FaceTime video poor quality', 'COMPLETED', 'NORMAL', 2500.00, 2300.00, 'Camera module replaced'),
('d1e2f3a4-0001-4000-8000-000000000018', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000016', 'b1c2d3e4-0001-4000-8000-000000000001', 'DESKTOP', 'Custom Ryzen Build', 'CSTM20240003', 'GPU artifacts on screen, crashes during gaming', 'COMPLETED', 'NORMAL', 4500.00, 4200.00, 'GPU thermal paste reapplied, fan cleaned, issue resolved'),
('d1e2f3a4-0001-4000-8000-000000000019', 'a1b2c3d4-0001-4000-8000-000000000002', 'c1d2e3f4-0001-4000-8000-000000000021', 'b1c2d3e4-0001-4000-8000-000000000003', 'LAPTOP', 'MacBook Pro 14 M3', 'C02Y98765432', 'Liquid spill — keyboard and trackpad not working', 'COMPLETED', 'URGENT', 12000.00, 11500.00, 'Board cleaned, keyboard replaced, trackpad cable reseated'),
-- In-progress tickets
('d1e2f3a4-0001-4000-8000-000000000020', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000022', 'b1c2d3e4-0001-4000-8000-000000000002', 'MOBILE', 'Google Pixel 8', 'GOOG12345ABC', 'Microphone not working during calls', 'IN_REPAIR', 'NORMAL', 1500.00, NULL, 'Mic module ordered, awaiting installation'),
('d1e2f3a4-0001-4000-8000-000000000021', 'a1b2c3d4-0001-4000-8000-000000000003', 'c1d2e3f4-0001-4000-8000-000000000025', 'b1c2d3e4-0001-4000-8000-000000000005', 'LAPTOP', 'HP Elitebook 840', 'HPE84012345A', 'Hinge broken, screen hanging loose', 'WAITING_FOR_PARTS', 'NORMAL', 3000.00, NULL, 'New hinge assembly on order from supplier'),
('d1e2f3a4-0001-4000-8000-000000000022', 'a1b2c3d4-0001-4000-8000-000000000002', 'c1d2e3f4-0001-4000-8000-000000000026', 'b1c2d3e4-0001-4000-8000-000000000004', 'DESKTOP', 'Dell OptiPlex 7080', 'DLLO7080ABCD', 'PSU making buzzing noise, intermittent shutdowns', 'DIAGNOSING', 'URGENT', 2500.00, NULL, NULL),
-- New open tickets
('d1e2f3a4-0001-4000-8000-000000000023', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000027', NULL, 'MOBILE', 'iPhone 14 Pro Max', 'DNXYZ9876543', 'Wireless charging not working', 'OPEN', 'NORMAL', 2000.00, NULL, NULL),
('d1e2f3a4-0001-4000-8000-000000000024', 'a1b2c3d4-0001-4000-8000-000000000003', 'c1d2e3f4-0001-4000-8000-000000000028', NULL, 'LAPTOP', 'Lenovo Legion 5', 'LENL5123ABCD', 'WiFi keeps disconnecting, Bluetooth not available', 'OPEN', 'NORMAL', 1200.00, NULL, NULL),
('d1e2f3a4-0001-4000-8000-000000000025', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000029', NULL, 'MOBILE', 'Xiaomi 13 Pro', 'XI13P567890A', 'Fingerprint sensor not recognizing, slow response', 'OPEN', 'NORMAL', 1800.00, NULL, NULL),
-- Ready for pickup
('d1e2f3a4-0001-4000-8000-000000000026', 'a1b2c3d4-0001-4000-8000-000000000002', 'c1d2e3f4-0001-4000-8000-000000000018', 'b1c2d3e4-0001-4000-8000-000000000003', 'LAPTOP', 'HP Pavilion Gaming', 'HPG15789ABCD', 'SSD failed, data recovery needed', 'READY_FOR_PICKUP', 'URGENT', 5500.00, 5200.00, 'Data recovered to new SSD, system restored'),
('d1e2f3a4-0001-4000-8000-000000000027', 'a1b2c3d4-0001-4000-8000-000000000001', 'c1d2e3f4-0001-4000-8000-000000000030', 'b1c2d3e4-0001-4000-8000-000000000001', 'DESKTOP', 'iMac 24 M1', 'CIMAC24M1ABC', 'Speaker crackling, USB ports not working', 'READY_FOR_PICKUP', 'NORMAL', 3800.00, 3500.00, 'Logic board component replaced, USB controller repaired'),
('d1e2f3a4-0001-4000-8000-000000000028', 'a1b2c3d4-0001-4000-8000-000000000003', 'c1d2e3f4-0001-4000-8000-000000000017', 'b1c2d3e4-0001-4000-8000-000000000006', 'MOBILE', 'Samsung Galaxy A54', 'RF8R30KLMNO', 'Charging port loose, only charges at angle', 'QA_CHECK', 'NORMAL', 650.00, 600.00, 'USB-C port replaced, testing charge cycles');

-- ═══════ MORE DIAGNOSIS STEPS ═══════
INSERT INTO diagnosis_step (ticket_id, step_order, name, result, notes) VALUES
('d1e2f3a4-0001-4000-8000-000000000015', 1, 'Touch screen zone test', 'PARTIAL_FAIL', 'Bottom 20% of screen unresponsive'),
('d1e2f3a4-0001-4000-8000-000000000015', 2, 'Display cable inspection', 'PASS', 'Cable connections secure'),
('d1e2f3a4-0001-4000-8000-000000000015', 3, 'Digitizer replacement', 'DONE', 'New digitizer installed and tested'),
('d1e2f3a4-0001-4000-8000-000000000019', 1, 'Liquid damage assessment', 'FAIL', 'Liquid detected under keyboard and trackpad'),
('d1e2f3a4-0001-4000-8000-000000000019', 2, 'Board cleaning with IPA', 'DONE', 'Ultrasonic cleaning completed'),
('d1e2f3a4-0001-4000-8000-000000000019', 3, 'Component testing post-clean', 'PARTIAL', 'Keyboard non-functional, trackpad intermittent'),
('d1e2f3a4-0001-4000-8000-000000000019', 4, 'Keyboard replacement', 'DONE', 'New top case with keyboard installed'),
('d1e2f3a4-0001-4000-8000-000000000019', 5, 'Full system test', 'PASS', 'All functions verified working'),
('d1e2f3a4-0001-4000-8000-000000000022', 1, 'PSU voltage test', 'RUNNING', 'Measuring all rail outputs under load');

-- ═══════ MORE TICKET TIMELINE ═══════
INSERT INTO ticket_timeline (ticket_id, action, detail) VALUES
('d1e2f3a4-0001-4000-8000-000000000015', 'CREATED', 'Walk-in customer, screen issue'),
('d1e2f3a4-0001-4000-8000-000000000015', 'STATUS_CHANGED', 'OPEN → DIAGNOSING'),
('d1e2f3a4-0001-4000-8000-000000000015', 'STATUS_CHANGED', 'DIAGNOSING → IN_REPAIR'),
('d1e2f3a4-0001-4000-8000-000000000015', 'STATUS_CHANGED', 'IN_REPAIR → COMPLETED'),
('d1e2f3a4-0001-4000-8000-000000000019', 'CREATED', 'Urgent liquid damage case'),
('d1e2f3a4-0001-4000-8000-000000000019', 'ASSIGNED', 'Assigned to Tech Deepak (specialist)'),
('d1e2f3a4-0001-4000-8000-000000000019', 'STATUS_CHANGED', 'OPEN → DIAGNOSING'),
('d1e2f3a4-0001-4000-8000-000000000019', 'STATUS_CHANGED', 'DIAGNOSING → IN_REPAIR'),
('d1e2f3a4-0001-4000-8000-000000000019', 'STATUS_CHANGED', 'IN_REPAIR → WAITING_FOR_PARTS'),
('d1e2f3a4-0001-4000-8000-000000000019', 'STATUS_CHANGED', 'WAITING_FOR_PARTS → IN_REPAIR'),
('d1e2f3a4-0001-4000-8000-000000000019', 'STATUS_CHANGED', 'IN_REPAIR → QA_CHECK'),
('d1e2f3a4-0001-4000-8000-000000000019', 'STATUS_CHANGED', 'QA_CHECK → COMPLETED'),
('d1e2f3a4-0001-4000-8000-000000000026', 'CREATED', 'SSD failure — urgent data recovery'),
('d1e2f3a4-0001-4000-8000-000000000026', 'ASSIGNED', 'Assigned to Tech Deepak'),
('d1e2f3a4-0001-4000-8000-000000000026', 'STATUS_CHANGED', 'OPEN → DIAGNOSING'),
('d1e2f3a4-0001-4000-8000-000000000026', 'STATUS_CHANGED', 'DIAGNOSING → IN_REPAIR'),
('d1e2f3a4-0001-4000-8000-000000000026', 'STATUS_CHANGED', 'IN_REPAIR → READY_FOR_PICKUP');
