package com.repairbro.commons.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Base envelope for all domain events published via Kafka.
 * Every event carries metadata for tracing, idempotency, and routing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainEvent {

    /** Unique event ID for idempotency */
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    /** Fully-qualified event type, e.g. "repairbro.repair-core.TicketCreated" */
    private String eventType;

    /** Aggregate root ID (e.g., ticketId, invoiceId) */
    private String aggregateId;

    /** Aggregate type (e.g., "Ticket", "Invoice") */
    private String aggregateType;

    /** Correlation ID for distributed tracing */
    private String correlationId;

    /** Source service that produced this event */
    private String source;

    /** Event timestamp */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Builder.Default
    private Instant timestamp = Instant.now();

    /** Schema version for forward/backward compatibility */
    @Builder.Default
    private int version = 1;

    /** Event payload as a flexible map */
    private Map<String, Object> payload;
}
