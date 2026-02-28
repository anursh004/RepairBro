package com.repairbro.repaircore.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "repair_ticket")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RepairTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private UUID branchId;

    @Column(nullable = false)
    private UUID customerId;

    /** Technician assigned to this ticket */
    private UUID assignedTechId;

    @Column(nullable = false, length = 30)
    private String deviceType;

    @Column(length = 128)
    private String deviceModel;

    @Column(length = 64)
    private String deviceSerial;

    @Column(columnDefinition = "TEXT")
    private String symptom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private TicketPriority priority = TicketPriority.NORMAL;

    @Column(precision = 12, scale = 2)
    private BigDecimal estimatedCost;

    @Column(precision = 12, scale = 2)
    private BigDecimal finalCost;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<DiagnosisStep> diagnosisSteps = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<TicketTimeline> timeline = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant completedAt;

    // ── Convenience methods ───────────────────────────────

    public void addDiagnosisStep(DiagnosisStep step) {
        step.setTicket(this);
        step.setStepOrder(this.diagnosisSteps.size() + 1);
        this.diagnosisSteps.add(step);
    }

    public void addTimelineEntry(String action, String detail, UUID performedBy) {
        TicketTimeline entry = TicketTimeline.builder()
                .ticket(this)
                .action(action)
                .detail(detail)
                .performedBy(performedBy)
                .build();
        this.timeline.add(entry);
    }
}
