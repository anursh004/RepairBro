package com.repairbro.fixbill.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repair cost estimate / quote given before actual work begins.
 * Can be converted to an Invoice after customer approval.
 */
@Entity
@Table(name = "estimate")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Estimate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private UUID ticketId;

    @Column(nullable = false)
    private UUID branchId;

    @Column(nullable = false)
    private UUID customerId;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal laborCost;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal partsCost;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = new BigDecimal("18.00");

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal taxAmount;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal totalEstimate;

    @Builder.Default
    private int estimatedDays = 2;

    @Column(columnDefinition = "TEXT")
    private String workDescription;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private EstimateStatus status = EstimateStatus.PENDING;

    @CreationTimestamp
    private Instant createdAt;

    private Instant approvedAt;

    public enum EstimateStatus {
        PENDING, APPROVED, REJECTED, CONVERTED_TO_INVOICE
    }
}
