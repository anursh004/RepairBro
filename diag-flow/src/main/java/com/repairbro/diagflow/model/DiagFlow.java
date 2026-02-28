package com.repairbro.diagflow.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A canonical diagnostic flow — reusable checklist template
 * for a specific device type + symptom category.
 */
@Entity
@Table(name = "diag_flow")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DiagFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 30)
    private String deviceType;

    @Column(length = 128)
    private String symptomCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private int version = 1;

    @OneToMany(mappedBy = "diagFlow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<DiagFlowStep> steps = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public void addStep(DiagFlowStep step) {
        step.setDiagFlow(this);
        step.setStepOrder(this.steps.size() + 1);
        this.steps.add(step);
    }
}
