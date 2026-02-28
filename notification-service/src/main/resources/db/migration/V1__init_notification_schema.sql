-- V1__init_notification_schema.sql

CREATE TABLE notification_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    channel VARCHAR(10) NOT NULL,  -- EMAIL, SMS, PUSH
    recipient VARCHAR(256) NOT NULL,
    subject VARCHAR(256),
    body TEXT NOT NULL,
    template_key VARCHAR(64),
    event_type VARCHAR(128),
    ticket_id UUID,
    customer_id UUID,
    branch_id UUID,
    status VARCHAR(20) DEFAULT 'SENT',  -- SENT, FAILED, QUEUED
    error_message TEXT,
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE notification_template (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_key VARCHAR(64) NOT NULL UNIQUE,
    channel VARCHAR(10) NOT NULL,
    subject VARCHAR(256),
    body_template TEXT NOT NULL,
    description VARCHAR(256),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_notif_log_ticket ON notification_log(ticket_id);
CREATE INDEX idx_notif_log_customer ON notification_log(customer_id);
CREATE INDEX idx_notif_log_status ON notification_log(status);
CREATE INDEX idx_notif_log_created ON notification_log(created_at);
CREATE INDEX idx_notif_template_key ON notification_template(template_key);
