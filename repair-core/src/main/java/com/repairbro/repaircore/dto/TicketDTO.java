package com.repairbro.repaircore.dto;

import com.repairbro.repaircore.model.TicketPriority;
import com.repairbro.repaircore.model.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {

    private UUID id;
    private UUID branchId;
    private UUID customerId;
    private UUID assignedTechId;
    private String deviceType;
    private String deviceModel;
    private String deviceSerial;
    private String symptom;
    private TicketStatus status;
    private TicketPriority priority;
    private BigDecimal estimatedCost;
    private BigDecimal finalCost;
    private String notes;
    private List<DiagnosisStepDTO> diagnosisSteps;
    private List<TimelineEntryDTO> timeline;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiagnosisStepDTO {
        private UUID id;
        private int stepOrder;
        private String name;
        private String result;
        private UUID performedBy;
        private String notes;
        private Instant createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineEntryDTO {
        private UUID id;
        private String action;
        private String detail;
        private UUID performedBy;
        private Instant createdAt;
    }
}
