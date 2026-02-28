package com.repairbro.fixbill.service;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.event.EventTopics;
import com.repairbro.commons.exception.RepairBroException;
import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.fixbill.model.Estimate;
import com.repairbro.fixbill.repository.EstimateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstimateService {

    private final EstimateRepository estimateRepo;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Transactional
    public Estimate createEstimate(UUID ticketId, UUID branchId, UUID customerId,
            BigDecimal laborCost, BigDecimal partsCost,
            int estimatedDays, String workDescription) {
        BigDecimal taxRate = new BigDecimal("18.00");
        BigDecimal subtotal = laborCost.add(partsCost);
        BigDecimal taxAmount = subtotal.multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(taxAmount);

        Estimate estimate = Estimate.builder()
                .ticketId(ticketId)
                .branchId(branchId)
                .customerId(customerId)
                .laborCost(laborCost)
                .partsCost(partsCost)
                .taxRate(taxRate)
                .taxAmount(taxAmount)
                .totalEstimate(total)
                .estimatedDays(estimatedDays)
                .workDescription(workDescription)
                .build();

        estimate = estimateRepo.save(estimate);
        log.info("Estimate created for ticket {}: total={}", ticketId, total);

        publishEvent("EstimateCreated", ticketId.toString(),
                Map.of("ticketId", ticketId.toString(), "total", total.toString()));
        return estimate;
    }

    @Transactional(readOnly = true)
    public Estimate getEstimate(UUID id) {
        return estimateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estimate", id.toString()));
    }

    @Transactional(readOnly = true)
    public Estimate getByTicket(UUID ticketId) {
        return estimateRepo.findByTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Estimate", "ticketId=" + ticketId));
    }

    @Transactional
    public Estimate approveEstimate(UUID id) {
        Estimate estimate = getEstimate(id);
        if (estimate.getStatus() != Estimate.EstimateStatus.PENDING) {
            throw new RepairBroException("Estimate is not in PENDING status",
                    HttpStatus.CONFLICT, "INVALID_STATUS");
        }
        estimate.setStatus(Estimate.EstimateStatus.APPROVED);
        estimate.setApprovedAt(Instant.now());
        estimate = estimateRepo.save(estimate);

        publishEvent("EstimateApproved", estimate.getTicketId().toString(),
                Map.of("estimateId", id.toString(), "ticketId", estimate.getTicketId().toString()));
        return estimate;
    }

    @Transactional
    public Estimate rejectEstimate(UUID id) {
        Estimate estimate = getEstimate(id);
        estimate.setStatus(Estimate.EstimateStatus.REJECTED);
        return estimateRepo.save(estimate);
    }

    @Transactional(readOnly = true)
    public List<Estimate> getByBranch(UUID branchId) {
        return estimateRepo.findByBranchId(branchId);
    }

    private void publishEvent(String type, String key, Map<String, String> payload) {
        try {
            DomainEvent event = DomainEvent.builder()
                    .eventType("repairbro.fix-bill." + type)
                    .aggregateType("Estimate").source("fix-bill")
                    .payload(Map.copyOf(payload)).build();
            kafkaTemplate.send(EventTopics.BILLING_EVENTS, key, event);
        } catch (Exception e) {
            log.warn("Failed to publish {}: {}", type, e.getMessage());
        }
    }
}
