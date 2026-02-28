package com.repairbro.notification.consumer;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.event.EventTopics;
import com.repairbro.notification.service.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Consumes events from all services and triggers real notifications
 * (email + SMS) using the NotificationDispatcher.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationDispatcher dispatcher;

    // ── Direct notification commands ─────────────────────────
    @KafkaListener(topics = EventTopics.NOTIFICATION_COMMANDS, groupId = "notification-group")
    public void handleNotificationCommand(DomainEvent event) {
        log.info("Notification command: {} [{}]", event.getEventType(), event.getAggregateId());
        Map<String, String> data = toStringMap(event.getPayload());

        String type = event.getEventType();

        if (type.contains("UserCreated")) {
            dispatcher.notifyWelcome(data);
        } else if (type.contains("TicketCreated")) {
            dispatcher.notifyTicketCreated(data);
        } else if (type.contains("EstimateCreated") || type.contains("EstimateReady")) {
            dispatcher.notifyEstimateReady(data);
        } else if (type.contains("PaymentReceived") || type.contains("PaymentCompleted")) {
            dispatcher.notifyPaymentReceived(data);
        } else if (type.contains("WarrantyIssued")) {
            dispatcher.notifyWarrantyIssued(data);
        } else {
            log.info("Unhandled notification command: {}", type);
        }
    }

    // ── Ticket lifecycle events ──────────────────────────────
    @KafkaListener(topics = EventTopics.TICKET_EVENTS, groupId = "notification-group")
    public void handleTicketEvents(DomainEvent event) {
        log.info("Ticket event: {} [{}]", event.getEventType(), event.getAggregateId());
        Map<String, String> data = toStringMap(event.getPayload());
        String type = event.getEventType();

        if (type.contains("TicketCreated")) {
            dispatcher.notifyTicketCreated(data);
        } else if (type.contains("TicketStatusChanged") || type.contains("StatusChanged")) {
            String newStatus = data.getOrDefault("newStatus", "");
            if ("READY_FOR_PICKUP".equals(newStatus)) {
                dispatcher.notifyReadyForPickup(data);
            } else if ("COMPLETED".equals(newStatus)) {
                dispatcher.notifyReadyForPickup(data);
            } else {
                dispatcher.notifyTicketStatusUpdate(data);
            }
        } else if (type.contains("TicketCompleted") || type.contains("TicketReady")) {
            dispatcher.notifyReadyForPickup(data);
        }
    }

    // ── Billing events ───────────────────────────────────────
    @KafkaListener(topics = EventTopics.BILLING_EVENTS, groupId = "notification-group")
    public void handleBillingEvents(DomainEvent event) {
        log.info("Billing event: {} [{}]", event.getEventType(), event.getAggregateId());
        Map<String, String> data = toStringMap(event.getPayload());
        String type = event.getEventType();

        if (type.contains("EstimateCreated")) {
            dispatcher.notifyEstimateReady(data);
        } else if (type.contains("PaymentReceived") || type.contains("PaymentCompleted")) {
            dispatcher.notifyPaymentReceived(data);
        }
    }

    // ── SLA events ───────────────────────────────────────────
    @KafkaListener(topics = EventTopics.SLA_EVENTS, groupId = "notification-group")
    public void handleSlaEvents(DomainEvent event) {
        log.info("SLA event: {} [{}]", event.getEventType(), event.getAggregateId());
        if (event.getEventType().contains("SLABreached") || event.getEventType().contains("Breached")) {
            Map<String, String> data = toStringMap(event.getPayload());
            dispatcher.notifySLABreach(data);
        }
    }

    // ── Diagnosis events ─────────────────────────────────────
    @KafkaListener(topics = EventTopics.DIAGNOSIS_EVENTS, groupId = "notification-group")
    public void handleDiagnosisEvents(DomainEvent event) {
        log.info("Diagnosis event: {} [{}]", event.getEventType(), event.getAggregateId());
        Map<String, String> data = toStringMap(event.getPayload());

        if (event.getEventType().contains("DiagnosisCompleted")) {
            // Notify customer that diagnosis is done
            String email = data.getOrDefault("customerEmail", "");
            if (!email.isBlank()) {
                dispatcher.sendEmail(email, "🔍 Diagnosis Complete — Repair Plan Ready",
                        "<p>Your device has been diagnosed. Root cause: <strong>"
                                + data.getOrDefault("rootCause", "See detailed report")
                                + "</strong></p><p>Confidence: " + data.getOrDefault("confidence", "N/A") + "</p>",
                        "DIAGNOSIS_COMPLETE", "DiagnosisCompleted",
                        null, null, null);
            }
        }
    }

    /**
     * Safely convert Map<String, Object> to Map<String, String>.
     */
    private Map<String, String> toStringMap(Map<String, Object> payload) {
        if (payload == null)
            return new HashMap<>();
        Map<String, String> result = new HashMap<>();
        payload.forEach((k, v) -> result.put(k, v != null ? v.toString() : ""));
        return result;
    }
}
