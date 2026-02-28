-- V3__bulk_data_1000.sql — 1000 notification logs + 1000 diagnostic evaluations

-- ═══════ 1000 NOTIFICATION LOGS ═══════
INSERT INTO notification_log (channel, recipient, subject, body, template_key, event_type, status, created_at)
SELECT
    (ARRAY['EMAIL','EMAIL','EMAIL','SMS','SMS','EMAIL','EMAIL','SMS','EMAIL','PUSH'])[1 + (s % 10)],
    CASE
        WHEN s % 10 IN (3,4,7) THEN '98' || lpad(((70000 + s)::text), 8, '0')
        ELSE lower(
            (ARRAY['aarav','vivaan','aditya','vihaan','arjun','sai','reyansh','ayaan','krishna','ishaan'])[1 + (s % 10)]
        ) || s || '@gmail.com'
    END,
    (ARRAY['Repair Ticket Created','Ticket Status Update','Your Device is Ready!',NULL,NULL,
           'Repair Estimate Ready','Payment Confirmation','SLA Breach Alert',NULL,
           'Welcome to RepairBro!','Warranty Certificate'])[1 + (s % 11)],
    'Notification body for record #' || s,
    (ARRAY['TICKET_CREATED','TICKET_STATUS_UPDATE','READY_FOR_PICKUP','TICKET_CREATED','READY_FOR_PICKUP',
           'ESTIMATE_READY','PAYMENT_CONFIRMATION','SLA_BREACH','TICKET_STATUS_UPDATE',
           'WELCOME','WARRANTY_ISSUED'])[1 + (s % 11)],
    (ARRAY['TicketCreated','TicketStatusChanged','TicketReadyForPickup','TicketCreated','TicketReadyForPickup',
           'EstimateCreated','PaymentReceived','SLABreached','TicketStatusChanged',
           'UserCreated','WarrantyIssued'])[1 + (s % 11)],
    -- 95% SENT, 3% FAILED, 2% QUEUED
    CASE
        WHEN s % 100 < 95 THEN 'SENT'
        WHEN s % 100 < 98 THEN 'FAILED'
        ELSE 'QUEUED'
    END,
    now() - ((s % 180)::text || ' days')::interval - ((s * 11 % 1440)::text || ' minutes')::interval
FROM generate_series(1, 1000) AS s;
