package com.repairbro.slaguard.service;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.event.EventTopics;
import com.repairbro.slaguard.model.*;
import com.repairbro.slaguard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlaService {

    private final SlaRecordRepository slaRepo;
    private final ComplaintRepository complaintRepo;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Transactional
    public SlaRecord createSla(UUID ticketId, UUID branchId, String slaType, Instant deadline) {
        SlaRecord sla = SlaRecord.builder()
                .ticketId(ticketId).branchId(branchId)
                .slaType(slaType).deadline(deadline).build();
        return slaRepo.save(sla);
    }

    @Transactional(readOnly = true)
    public List<SlaRecord> getByBranch(UUID branchId) {
        return slaRepo.findByBranchId(branchId);
    }

    /** Scheduled job runs every 5 minutes to detect SLA breaches */
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void checkBreaches() {
        List<SlaRecord> breached = slaRepo.findBreachedSlas(Instant.now());
        for (SlaRecord sla : breached) {
            sla.setStatus(SlaRecord.SlaStatus.BREACHED);
            slaRepo.save(sla);
            log.warn("SLA BREACHED: ticket={} type={}", sla.getTicketId(), sla.getSlaType());
            publishEvent("SLABreached", sla.getTicketId().toString(),
                    Map.of("ticketId", sla.getTicketId().toString(),
                            "branchId", sla.getBranchId().toString(),
                            "slaType", sla.getSlaType()));
        }
    }

    @Transactional
    public Complaint fileComplaint(Complaint complaint) {
        complaint = complaintRepo.save(complaint);
        publishEvent("ComplaintRaised", complaint.getTicketId().toString(),
                Map.of("complaintId", complaint.getId().toString()));
        return complaint;
    }

    @Transactional(readOnly = true)
    public List<Complaint> getComplaints(UUID branchId) {
        return complaintRepo.findByBranchId(branchId);
    }

    private void publishEvent(String type, String key, Map<String, String> payload) {
        try {
            DomainEvent event = DomainEvent.builder()
                    .eventType("repairbro.sla-guard." + type)
                    .aggregateType("SLA").source("sla-guard")
                    .payload(Map.copyOf(payload)).build();
            kafkaTemplate.send(EventTopics.SLA_EVENTS, key, event);
        } catch (Exception e) {
            log.warn("Failed to publish {}: {}", type, e.getMessage());
        }
    }
}
