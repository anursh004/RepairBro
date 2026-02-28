-- V5__bulk_data_1000.sql — 1000 customers + 1000 tickets using generate_series()
-- Uses PostgreSQL functions for realistic, randomized data

-- ═══════ 1000 CUSTOMERS ═══════
INSERT INTO customer (id, name, phone, email, address, city, pincode, preferred_channel, total_repairs)
SELECT
    gen_random_uuid(),
    (ARRAY['Aarav','Vivaan','Aditya','Vihaan','Arjun','Sai','Reyansh','Ayaan','Krishna','Ishaan',
           'Ananya','Diya','Saanvi','Anika','Aadhya','Myra','Sara','Pari','Ira','Navya',
           'Rahul','Priya','Amit','Sneha','Vikram','Deepa','Suresh','Kavita','Rohit','Pooja'])[1 + (s % 30)]
    || ' ' ||
    (ARRAY['Sharma','Patel','Gupta','Singh','Kumar','Reddy','Joshi','Verma','Malhotra','Nair',
           'Chopra','Bhat','Rao','Pillai','Desai','Kulkarni','Iyer','Chauhan','Saxena','Thakur',
           'Agarwal','Menon','Shah','Mishra','Das','Raman','Goyal','Kapoor','Deshpande','Tiwari'])[1 + ((s * 7) % 30)],
    '98' || lpad(((70000 + s)::text), 8, '0'),
    lower(
        (ARRAY['aarav','vivaan','aditya','vihaan','arjun','sai','reyansh','ayaan','krishna','ishaan',
               'ananya','diya','saanvi','anika','aadhya','myra','sara','pari','ira','navya',
               'rahul','priya','amit','sneha','vikram','deepa','suresh','kavita','rohit','pooja'])[1 + (s % 30)]
    ) || s || '@' || (ARRAY['gmail.com','yahoo.com','outlook.com','hotmail.com'])[1 + (s % 4)],
    (s % 200 + 1)::text || ' ' ||
    (ARRAY['MG Road','Station Road','Park Street','Civil Lines','Gandhi Nagar','Nehru Place',
           'Sector '||(s%50+1)::text,'Laxmi Nagar','Rajaji Road','Jubilee Hills'])[1 + (s % 10)],
    (ARRAY['Mumbai','Pune','Nagpur','Bangalore','Chennai','Hyderabad','Kolkata','Delhi','Ahmedabad','Jaipur',
           'Lucknow','Indore','Kochi','Gurgaon','Noida','Chandigarh','Bhopal','Surat','Vadodara','Coimbatore'])[1 + (s % 20)],
    (ARRAY['400001','411001','440001','560001','600001','500001','700001','110001','380001','302001',
           '226001','452001','682001','122001','201301','160001','462001','395001','390001','641001'])[1 + (s % 20)],
    (ARRAY['SMS','EMAIL','WHATSAPP'])[1 + (s % 3)],
    (s * 3) % 12  -- 0-11 past repairs
FROM generate_series(100, 1099) AS s;

-- ═══════ 1000 REPAIR TICKETS ═══════
INSERT INTO repair_ticket (id, branch_id, customer_id, device_type, device_model, device_serial, symptom, status, priority, estimated_cost, final_cost, notes, created_at)
SELECT
    gen_random_uuid(),
    -- Distribute across 3 branches: 50% Mumbai, 30% Pune, 20% Nagpur
    CASE
        WHEN s % 10 < 5 THEN 'a1b2c3d4-0001-4000-8000-000000000001'::uuid
        WHEN s % 10 < 8 THEN 'a1b2c3d4-0001-4000-8000-000000000002'::uuid
        ELSE 'a1b2c3d4-0001-4000-8000-000000000003'::uuid
    END,
    -- Pick from existing customers (use the 30 seeded ones for FK compliance)
    (ARRAY[
        'c1d2e3f4-0001-4000-8000-000000000001','c1d2e3f4-0001-4000-8000-000000000002','c1d2e3f4-0001-4000-8000-000000000003',
        'c1d2e3f4-0001-4000-8000-000000000004','c1d2e3f4-0001-4000-8000-000000000005','c1d2e3f4-0001-4000-8000-000000000006',
        'c1d2e3f4-0001-4000-8000-000000000007','c1d2e3f4-0001-4000-8000-000000000008','c1d2e3f4-0001-4000-8000-000000000009',
        'c1d2e3f4-0001-4000-8000-000000000010','c1d2e3f4-0001-4000-8000-000000000011','c1d2e3f4-0001-4000-8000-000000000012',
        'c1d2e3f4-0001-4000-8000-000000000013','c1d2e3f4-0001-4000-8000-000000000014','c1d2e3f4-0001-4000-8000-000000000015'
    ]::uuid[])[1 + (s % 15)],
    -- Device types
    (ARRAY['LAPTOP','MOBILE','DESKTOP','MOBILE','LAPTOP','MOBILE'])[1 + (s % 6)],
    -- Device models
    (ARRAY['MacBook Air M2','iPhone 15 Pro','Dell XPS 15','Samsung Galaxy S24','Lenovo ThinkPad X1','OnePlus 12',
           'HP Pavilion 14','iPad Pro 12.9','ASUS ROG Strix','Google Pixel 8',
           'MacBook Pro 14','iPhone 14','Acer Aspire 5','Xiaomi 14','HP Elitebook 840',
           'Samsung Galaxy A54','Lenovo Legion 5','Microsoft Surface Pro','Realme GT','Vivo X100'])[1 + (s % 20)],
    'SN-' || lpad(s::text, 6, '0') || '-' || chr(65 + (s % 26)),
    -- Symptoms
    (ARRAY['Screen cracked after drop','Not powering on','Battery draining fast','Keyboard not responding',
           'WiFi connectivity issues','Charging port loose','Speaker not working','Camera blurry',
           'Overheating during use','Blue screen errors','Touch screen unresponsive','Water damage',
           'Microphone not working','Hinge broken','SSD failed','Fingerprint sensor not working',
           'Bluetooth not connecting','Display flickering','USB ports not working','Fan making noise'])[1 + (s % 20)],
    -- Status distribution: 40% COMPLETED, 15% IN_REPAIR, 10% OPEN, 10% DIAGNOSING, 10% READY_FOR_PICKUP, 5% WAITING, 5% QA, 5% CANCELLED
    (ARRAY['COMPLETED','COMPLETED','COMPLETED','COMPLETED','IN_REPAIR','IN_REPAIR',
           'OPEN','OPEN','DIAGNOSING','READY_FOR_PICKUP','WAITING_FOR_PARTS','QA_CHECK',
           'COMPLETED','COMPLETED','COMPLETED','COMPLETED','IN_REPAIR',
           'OPEN','DIAGNOSING','COMPLETED'])[1 + (s % 20)],
    -- Priority: 80% NORMAL, 20% URGENT
    CASE WHEN s % 5 = 0 THEN 'URGENT' ELSE 'NORMAL' END,
    -- Estimated cost: ₹500 - ₹15000
    500 + (s * 37 % 14500),
    -- Final cost (for COMPLETED tickets only)
    CASE WHEN s % 20 IN (0,1,2,3,12,13,14,15,19) THEN (450 + (s * 31 % 13000))::numeric ELSE NULL END,
    -- Notes for completed
    CASE WHEN s % 20 IN (0,1,2,3,12,13,14,15,19) THEN 'Repair completed successfully' ELSE NULL END,
    -- Spread over last 90 days
    now() - ((s % 90)::text || ' days')::interval - ((s * 7 % 720)::text || ' minutes')::interval
FROM generate_series(100, 1099) AS s;

-- ═══════ TIMELINE ENTRIES for tickets (≈2000 entries) ═══════
INSERT INTO ticket_timeline (ticket_id, action, detail, created_at)
SELECT
    rt.id,
    'CREATED',
    'Ticket created via walk-in',
    rt.created_at
FROM repair_ticket rt
WHERE rt.created_at > now() - interval '90 days'
LIMIT 1000;
