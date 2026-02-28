-- V2__seed_notification_templates.sql

INSERT INTO notification_template (template_key, channel, subject, body_template, description) VALUES
('TICKET_CREATED', 'EMAIL', 'Repair Ticket Created — {ticketId}', 'Your repair ticket has been created for {deviceModel}. We will begin diagnosing shortly.', 'Sent when a new ticket is created'),
('TICKET_STATUS_UPDATE', 'EMAIL', 'Ticket Update — {newStatus}', 'Your ticket {ticketId} has been updated to {newStatus}.', 'Sent on every ticket status change'),
('READY_FOR_PICKUP', 'EMAIL', 'Your Device is Ready!', 'Your repair is complete. Visit {branchName} to collect your device. Amount due: ₹{finalCost}', 'Sent when device is ready for pickup'),
('ESTIMATE_READY', 'EMAIL', 'Repair Estimate Ready — ₹{totalEstimate}', 'We have prepared a cost estimate for your repair: ₹{totalEstimate}', 'Sent when a cost estimate is generated'),
('PAYMENT_CONFIRMATION', 'EMAIL', 'Payment Received — ₹{amount}', 'We received your payment of ₹{amount} via {method}. Transaction: {transactionId}', 'Sent after successful payment'),
('SLA_BREACH', 'EMAIL', 'SLA Breach Alert — Ticket {ticketId}', 'A ticket has breached its SLA deadline. Immediate action required.', 'Sent to branch manager on SLA breach'),
('WELCOME', 'EMAIL', 'Welcome to RepairBro!', 'Your account has been created. Email: {email}', 'Sent on user registration'),
('WARRANTY_ISSUED', 'EMAIL', 'Warranty Certificate — {warrantyDays} Days', 'Your repair comes with a {warrantyDays}-day warranty valid until {expiryDate}.', 'Sent after repair completion'),
('DIAGNOSIS_COMPLETE', 'EMAIL', 'Diagnosis Complete', 'Your device has been diagnosed. Root cause: {rootCause}', 'Sent when diagnosis is finished'),
-- SMS templates
('TICKET_CREATED', 'SMS', NULL, 'RepairBro: Ticket {ticketId} created for {deviceModel}. We will keep you updated!', 'SMS when ticket created'),
('READY_FOR_PICKUP', 'SMS', NULL, 'RepairBro: Your device is ready for pickup at {branchName}. Amount: ₹{finalCost}', 'SMS when ready for pickup'),
('TICKET_STATUS_UPDATE', 'SMS', NULL, 'RepairBro: Ticket {ticketId} updated to {newStatus}.', 'SMS on status change'),
('SLA_BREACH', 'SMS', NULL, 'RepairBro URGENT: SLA breached for ticket {ticketId}. Escalate immediately.', 'SMS to manager on SLA breach');
