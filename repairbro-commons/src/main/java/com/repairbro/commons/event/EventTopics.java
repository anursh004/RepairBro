package com.repairbro.commons.event;

/**
 * Constants for Kafka topic names across all services.
 * Centralized here to prevent typos and ensure consistency.
 */
public final class EventTopics {

    private EventTopics() {
    }

    // ── Repair Core ───────────────────────────────────────
    public static final String TICKET_EVENTS = "repairbro.repair-core.ticket-events";

    // ── DiagFlow ──────────────────────────────────────────
    public static final String DIAGNOSIS_EVENTS = "repairbro.diag-flow.diagnosis-events";

    // ── PartsFlow ─────────────────────────────────────────
    public static final String INVENTORY_EVENTS = "repairbro.parts-flow.inventory-events";

    // ── FixBill ───────────────────────────────────────────
    public static final String BILLING_EVENTS = "repairbro.fix-bill.billing-events";

    // ── SLAGuard ──────────────────────────────────────────
    public static final String SLA_EVENTS = "repairbro.sla-guard.sla-events";

    // ── FranchiseHub ──────────────────────────────────────
    public static final String FRANCHISE_EVENTS = "repairbro.franchise-hub.franchise-events";

    // ── Commands (async triggers) ─────────────────────────
    public static final String NOTIFICATION_COMMANDS = "repairbro.commands.notifications";
}
