package com.repairbro.slaguard.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "complaint")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private UUID ticketId;
    @Column(nullable = false)
    private UUID customerId;
    @Column(nullable = false)
    private UUID branchId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @CreationTimestamp
    private Instant createdAt;
    private Instant resolvedAt;

    public enum ComplaintStatus {
        OPEN, INVESTIGATING, RESOLVED, ESCALATED
    }
}
