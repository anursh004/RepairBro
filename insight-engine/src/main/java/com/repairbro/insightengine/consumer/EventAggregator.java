package com.repairbro.insightengine.consumer;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.event.EventTopics;
import com.repairbro.insightengine.model.BranchKpiDaily;
import com.repairbro.insightengine.repository.BranchKpiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * CQRS event consumer — listens to ALL domain events and builds
 * BranchKpiDaily read models for dashboards and reporting.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventAggregator {

    private final BranchKpiRepository kpiRepo;

    @KafkaListener(topics = {
            EventTopics.TICKET_EVENTS,
            EventTopics.BILLING_EVENTS,
            EventTopics.SLA_EVENTS,
            EventTopics.INVENTORY_EVENTS
    }, groupId = "insight-engine-group")
    @Transactional
    public void aggregateEvent(DomainEvent event) {
        log.info("[INSIGHT] Consumed: {} from {}", event.getEventType(), event.getSource());

        Map<String, Object> payload = event.getPayload();
        if (payload == null)
            return;

        String branchIdStr = payload.containsKey("branchId")
                ? payload.get("branchId").toString()
                : null;
        if (branchIdStr == null)
            return;

        UUID branchId;
        try {
            branchId = UUID.fromString(branchIdStr);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid branchId in event: {}", branchIdStr);
            return;
        }

        LocalDate today = LocalDate.now();

        // Find or create today's KPI row for this branch
        BranchKpiDaily kpi = kpiRepo.findByBranchIdAndDateBetween(branchId, today, today)
                .stream().findFirst()
                .orElseGet(() -> kpiRepo.save(BranchKpiDaily.builder()
                        .branchId(branchId).date(today)
                        .ticketsCreated(0).ticketsCompleted(0).slBreaches(0)
                        .revenue(BigDecimal.ZERO).mttrHours(0).ftfr(0).techUtilization(0)
                        .build()));

        String type = event.getEventType();

        // Aggregate based on event type
        if (type.contains("TicketCreated")) {
            kpi.setTicketsCreated(kpi.getTicketsCreated() + 1);
        } else if (type.contains("TicketCompleted") || type.contains("StatusChanged")) {
            String status = payload.containsKey("newStatus") ? payload.get("newStatus").toString() : "";
            if ("COMPLETED".equals(status) || type.contains("TicketCompleted")) {
                kpi.setTicketsCompleted(kpi.getTicketsCompleted() + 1);
            }
        } else if (type.contains("SLABreached")) {
            kpi.setSlBreaches(kpi.getSlBreaches() + 1);
        } else if (type.contains("PaymentReceived") || type.contains("PaymentCompleted")) {
            String amountStr = payload.containsKey("amount") ? payload.get("amount").toString() : "0";
            try {
                BigDecimal amount = new BigDecimal(amountStr);
                kpi.setRevenue(kpi.getRevenue().add(amount));
            } catch (NumberFormatException e) {
                log.warn("Invalid payment amount: {}", amountStr);
            }
        }

        kpiRepo.save(kpi);
        log.debug("[INSIGHT] Updated KPI for branch {} on {}", branchId, today);
    }
}
