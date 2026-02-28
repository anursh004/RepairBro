package com.repairbro.diagflow.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "diag_flow_step")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DiagFlowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diag_flow_id", nullable = false)
    @ToString.Exclude
    private DiagFlow diagFlow;

    @Column(nullable = false)
    private int stepOrder;

    @Column(nullable = false, length = 255)
    private String instruction;

    @Column(length = 64)
    private String expectedOutcome;

    /** Confidence weight (0.0 to 1.0) for this step's diagnostic value */
    @Builder.Default
    private double confidenceWeight = 0.5;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
