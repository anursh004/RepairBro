-- V1__init_billing_schema.sql
-- FixBill service database schema

CREATE TABLE invoice (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_number VARCHAR(32) NOT NULL UNIQUE,
    ticket_id UUID NOT NULL UNIQUE,
    branch_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    subtotal NUMERIC(12, 2) NOT NULL,
    tax_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    tax_rate NUMERIC(5, 2) DEFAULT 18.00,
    discount NUMERIC(12, 2) DEFAULT 0,
    total_amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    paid_at TIMESTAMP
);

CREATE TABLE invoice_line_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoice(id) ON DELETE CASCADE,
    description VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price NUMERIC(12, 2) NOT NULL,
    line_total NUMERIC(12, 2) NOT NULL,
    spare_part_id UUID
);

CREATE TABLE payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoice(id) ON DELETE CASCADE,
    amount NUMERIC(12, 2) NOT NULL,
    method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(128),
    gateway_ref VARCHAR(128),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    completed_at TIMESTAMP
);

-- Indexes
CREATE INDEX idx_invoice_ticket_id ON invoice(ticket_id);
CREATE INDEX idx_invoice_branch_id ON invoice(branch_id);
CREATE INDEX idx_invoice_customer_id ON invoice(customer_id);
CREATE INDEX idx_invoice_status ON invoice(status);
CREATE INDEX idx_line_item_invoice_id ON invoice_line_item(invoice_id);
CREATE INDEX idx_payment_invoice_id ON payment(invoice_id);
