package com.repairbro.diagflow.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Result of evaluating a diagnostic flow against a specific ticket context.
 */
@Entity
@Table(name = "diagnosis_evaluation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DiagnosisEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private UUID ticketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diag_flow_id")
    @ToString.Exclude
    private DiagFlow diagFlow;

    @Column(nullable = false)
    @Builder.Default
    private double confidenceScore = 0.0;

    @Column(columnDefinition = "TEXT")
    private String suggestedActions;

    @Column(columnDefinition = "TEXT")
    private String rootCause;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private EvalStatus status = EvalStatus.PENDING;

    @CreationTimestamp
    private Instant createdAt;

    public enum EvalStatus {
        PENDING, IN_PROGRESS, COMPLETED, INCONCLUSIVE
    }
}
