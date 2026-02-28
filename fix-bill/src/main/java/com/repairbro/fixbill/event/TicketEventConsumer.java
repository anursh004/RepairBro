package com.repairbro.fixbill.event;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.event.EventTopics;
import com.repairbro.fixbill.model.Invoice;
import com.repairbro.fixbill.model.InvoiceStatus;
import com.repairbro.fixbill.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Listens to ticket events from repair-core.
 * Auto-finalizes DRAFT invoices when a ticket is completed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TicketEventConsumer {

    private final InvoiceRepository invoiceRepository;

    @KafkaListener(topics = EventTopics.TICKET_EVENTS, groupId = "fix-bill-group", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void handleTicketEvent(DomainEvent event) {
        log.info("Received ticket event: {} for {}", event.getEventType(), event.getAggregateId());

        switch (event.getEventType()) {
            case "repairbro.repair-core.TicketCompleted" -> handleTicketCompleted(event);
            case "repairbro.repair-core.TicketCreated" -> log.debug("Ticket created: {}", event.getAggregateId());
            default -> log.debug("Ignoring event type: {}", event.getEventType());
        }
    }

    private void handleTicketCompleted(DomainEvent event) {
        String ticketIdStr = event.getAggregateId();
        log.info("Ticket completed: {} — auto-finalizing invoice", ticketIdStr);

        try {
            UUID ticketId = UUID.fromString(ticketIdStr);

            invoiceRepository.findByTicketId(ticketId).ifPresent(invoice -> {
                if (invoice.getStatus() == InvoiceStatus.DRAFT) {
                    invoice.setStatus(InvoiceStatus.SENT);
                    invoiceRepository.save(invoice);
                    log.info("Invoice {} auto-finalized to SENT for ticket {}",
                            invoice.getInvoiceNumber(), ticketId);
                } else {
                    log.info("Invoice {} already in status {}, no action needed",
                            invoice.getInvoiceNumber(), invoice.getStatus());
                }
            });
        } catch (IllegalArgumentException e) {
            log.warn("Invalid ticket ID in event: {}", ticketIdStr);
        }
    }
}
