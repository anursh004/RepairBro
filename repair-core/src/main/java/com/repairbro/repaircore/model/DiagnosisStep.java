package com.repairbro.repaircore.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "diagnosis_step")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DiagnosisStep {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @ToString.Exclude
    private RepairTicket ticket;

    @Column(nullable = false)
    private int stepOrder;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 64)
    private String result;

    private UUID performedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    private Instant createdAt;
}
