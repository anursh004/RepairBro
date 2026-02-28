package com.repairbro.repaircore.service;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.event.EventTopics;
import com.repairbro.commons.exception.RepairBroException;
import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.repaircore.dto.*;
import com.repairbro.repaircore.model.*;
import com.repairbro.repaircore.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Transactional
    public TicketDTO createTicket(CreateTicketRequest request) {
        RepairTicket ticket = RepairTicket.builder()
                .branchId(request.getBranchId())
                .customerId(request.getCustomerId())
                .deviceType(request.getDeviceType())
                .deviceModel(request.getDeviceModel())
                .deviceSerial(request.getDeviceSerial())
                .symptom(request.getSymptom())
                .priority(request.getPriority() != null ? request.getPriority() : TicketPriority.NORMAL)
                .estimatedCost(request.getEstimatedCost())
                .notes(request.getNotes())
                .build();

        ticket.addTimelineEntry("CREATED", "Ticket created", null);
        ticket = ticketRepository.save(ticket);

        log.info("Ticket created: {} for branch {}", ticket.getId(), ticket.getBranchId());
        publishEvent("TicketCreated", ticket);

        return toDTO(ticket);
    }

    @Transactional(readOnly = true)
    public TicketDTO getTicket(UUID ticketId) {
        RepairTicket ticket = findTicketOrThrow(ticketId);
        return toDTO(ticket);
    }

    @Transactional(readOnly = true)
    public Page<TicketDTO> getTicketsByBranch(UUID branchId, TicketStatus status, Pageable pageable) {
        Page<RepairTicket> page;
        if (status != null) {
            page = ticketRepository.findByBranchIdAndStatus(branchId, status, pageable);
        } else {
            page = ticketRepository.findByBranchId(branchId, pageable);
        }
        return page.map(this::toDTO);
    }

    @Transactional
    public TicketDTO updateStatus(UUID ticketId, UpdateTicketStatusRequest request) {
        RepairTicket ticket = findTicketOrThrow(ticketId);

        validateStatusTransition(ticket.getStatus(), request.getStatus());

        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(request.getStatus());

        if (request.getFinalCost() != null) {
            ticket.setFinalCost(request.getFinalCost());
        }

        if (request.getStatus() == TicketStatus.COMPLETED) {
            ticket.setCompletedAt(Instant.now());
        }

        String detail = String.format("Status changed: %s → %s", oldStatus, request.getStatus());
        if (request.getNotes() != null) {
            detail += " | " + request.getNotes();
        }
        ticket.addTimelineEntry("STATUS_CHANGED", detail, request.getPerformedBy());

        ticket = ticketRepository.save(ticket);
        log.info("Ticket {} status: {} → {}", ticketId, oldStatus, request.getStatus());
        publishEvent("StatusChanged", ticket);

        return toDTO(ticket);
    }

    @Transactional
    public TicketDTO addDiagnosis(UUID ticketId, AddDiagnosisRequest request) {
        RepairTicket ticket = findTicketOrThrow(ticketId);

        if (ticket.getStatus() == TicketStatus.COMPLETED || ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new RepairBroException(
                    "Cannot add diagnosis to a closed ticket",
                    HttpStatus.BAD_REQUEST,
                    "TICKET_CLOSED");
        }

        // Auto-transition to DIAGNOSING if still OPEN
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.DIAGNOSING);
            ticket.addTimelineEntry("STATUS_CHANGED", "Auto-transitioned to DIAGNOSING", request.getPerformedBy());
        }

        DiagnosisStep step = DiagnosisStep.builder()
                .name(request.getName())
                .result(request.getResult())
                .performedBy(request.getPerformedBy())
                .notes(request.getNotes())
                .build();

        ticket.addDiagnosisStep(step);
        ticket.addTimelineEntry("DIAGNOSIS_ADDED", "Step: " + request.getName(), request.getPerformedBy());

        ticket = ticketRepository.save(ticket);
        log.info("Diagnosis step added to ticket {}: {}", ticketId, request.getName());

        return toDTO(ticket);
    }

    @Transactional
    public TicketDTO assignTechnician(UUID ticketId, UUID techId) {
        RepairTicket ticket = findTicketOrThrow(ticketId);
        ticket.setAssignedTechId(techId);
        ticket.addTimelineEntry("TECH_ASSIGNED", "Technician assigned: " + techId, null);
        ticket = ticketRepository.save(ticket);
        return toDTO(ticket);
    }

    // ── Helpers ───────────────────────────────────────────

    private RepairTicket findTicketOrThrow(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId.toString()));
    }

    private void validateStatusTransition(TicketStatus current, TicketStatus next) {
        // Define valid transitions
        boolean valid = switch (current) {
            case OPEN -> next == TicketStatus.DIAGNOSING || next == TicketStatus.CANCELLED;
            case DIAGNOSING -> next == TicketStatus.WAITING_FOR_PARTS || next == TicketStatus.IN_REPAIR
                    || next == TicketStatus.CANCELLED;
            case WAITING_FOR_PARTS -> next == TicketStatus.IN_REPAIR || next == TicketStatus.CANCELLED;
            case IN_REPAIR -> next == TicketStatus.QA_CHECK || next == TicketStatus.CANCELLED;
            case QA_CHECK -> next == TicketStatus.READY_FOR_PICKUP || next == TicketStatus.IN_REPAIR;
            case READY_FOR_PICKUP -> next == TicketStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!valid) {
            throw new RepairBroException(
                    String.format("Invalid status transition: %s → %s", current, next),
                    HttpStatus.BAD_REQUEST,
                    "INVALID_STATUS_TRANSITION");
        }
    }

    private void publishEvent(String type, RepairTicket ticket) {
        try {
            DomainEvent event = DomainEvent.builder()
                    .eventType("repairbro.repair-core." + type)
                    .aggregateId(ticket.getId().toString())
                    .aggregateType("Ticket")
                    .source("repair-core")
                    .payload(Map.of(
                            "ticketId", ticket.getId().toString(),
                            "branchId", ticket.getBranchId().toString(),
                            "status", ticket.getStatus().name(),
                            "deviceType", ticket.getDeviceType(),
                            "priority", ticket.getPriority().name()))
                    .build();
            kafkaTemplate.send(EventTopics.TICKET_EVENTS, ticket.getId().toString(), event);
        } catch (Exception e) {
            log.warn("Failed to publish {} event for ticket {}: {}", type, ticket.getId(), e.getMessage());
        }
    }

    private TicketDTO toDTO(RepairTicket ticket) {
        return TicketDTO.builder()
                .id(ticket.getId())
                .branchId(ticket.getBranchId())
                .customerId(ticket.getCustomerId())
                .assignedTechId(ticket.getAssignedTechId())
                .deviceType(ticket.getDeviceType())
                .deviceModel(ticket.getDeviceModel())
                .deviceSerial(ticket.getDeviceSerial())
                .symptom(ticket.getSymptom())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .estimatedCost(ticket.getEstimatedCost())
                .finalCost(ticket.getFinalCost())
                .notes(ticket.getNotes())
                .diagnosisSteps(ticket.getDiagnosisSteps().stream()
                        .map(s -> TicketDTO.DiagnosisStepDTO.builder()
                                .id(s.getId())
                                .stepOrder(s.getStepOrder())
                                .name(s.getName())
                                .result(s.getResult())
                                .performedBy(s.getPerformedBy())
                                .notes(s.getNotes())
                                .createdAt(s.getCreatedAt())
                                .build())
                        .toList())
                .timeline(ticket.getTimeline().stream()
                        .map(t -> TicketDTO.TimelineEntryDTO.builder()
                                .id(t.getId())
                                .action(t.getAction())
                                .detail(t.getDetail())
                                .performedBy(t.getPerformedBy())
                                .createdAt(t.getCreatedAt())
                                .build())
                        .toList())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .completedAt(ticket.getCompletedAt())
                .build();
    }
}
