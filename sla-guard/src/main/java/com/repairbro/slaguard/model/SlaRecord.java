package com.repairbro.slaguard.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sla_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SlaRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private UUID ticketId;
    @Column(nullable = false)
    private UUID branchId;

    @Column(nullable = false, length = 30)
    private String slaType; // DIAGNOSTICS_24H, QUICK_FIX_48H, ADVANCED_7D

    @Column(nullable = false)
    private Instant deadline;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private SlaStatus status = SlaStatus.ACTIVE;

    private Instant resolvedAt;

    @CreationTimestamp
    private Instant createdAt;

    public enum SlaStatus {
        ACTIVE, MET, BREACHED
    }
}
