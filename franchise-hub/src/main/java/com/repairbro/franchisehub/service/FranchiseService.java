package com.repairbro.franchisehub.service;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.event.EventTopics;
import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.franchisehub.model.*;
import com.repairbro.franchisehub.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseRepository franchiseRepo;
    private final RoyaltyRepository royaltyRepo;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Transactional
    public Franchise onboard(Franchise franchise) {
        franchise = franchiseRepo.save(franchise);
        log.info("Franchise onboarded: {} [{}]", franchise.getBusinessName(), franchise.getId());
        publishEvent("BranchOnboarded", franchise.getId().toString(),
                Map.of("franchiseId", franchise.getId().toString(), "branchId", franchise.getBranchId().toString()));
        return franchise;
    }

    @Transactional(readOnly = true)
    public Franchise get(UUID id) {
        return franchiseRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Franchise", id.toString()));
    }

    @Transactional(readOnly = true)
    public List<Franchise> getAll() {
        return franchiseRepo.findAll();
    }

    @Transactional
    public RoyaltyRecord calculateRoyalty(UUID franchiseId, String period, BigDecimal grossRevenue) {
        Franchise f = get(franchiseId);
        BigDecimal royaltyAmount = grossRevenue.multiply(f.getRoyaltyPercent())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        RoyaltyRecord record = RoyaltyRecord.builder()
                .franchiseId(franchiseId).branchId(f.getBranchId()).period(period)
                .grossRevenue(grossRevenue).royaltyPercent(f.getRoyaltyPercent())
                .royaltyAmount(royaltyAmount).build();
        record = royaltyRepo.save(record);
        publishEvent("RoyaltyCalculated", franchiseId.toString(),
                Map.of("amount", royaltyAmount.toString(), "period", period));
        return record;
    }

    @Transactional(readOnly = true)
    public List<RoyaltyRecord> getRoyalties(UUID franchiseId) {
        return royaltyRepo.findByFranchiseId(franchiseId);
    }

    private void publishEvent(String type, String key, Map<String, String> payload) {
        try {
            DomainEvent event = DomainEvent.builder()
                    .eventType("repairbro.franchise-hub." + type)
                    .aggregateType("Franchise").source("franchise-hub")
                    .payload(Map.copyOf(payload)).build();
            kafkaTemplate.send(EventTopics.FRANCHISE_EVENTS, key, event);
        } catch (Exception e) {
            log.warn("Failed to publish {}: {}", type, e.getMessage());
        }
    }
}
