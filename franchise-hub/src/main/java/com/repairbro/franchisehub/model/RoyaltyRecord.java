package com.repairbro.franchisehub.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(name = "royalty_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoyaltyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private UUID franchiseId;

    @Column(nullable = false)
    private UUID branchId;

    @Column(nullable = false, length = 7)
    private String period; // "2026-02"

    @Column(precision = 12, scale = 2)
    private BigDecimal grossRevenue;

    @Column(precision = 5, scale = 2)
    private BigDecimal royaltyPercent;

    @Column(precision = 12, scale = 2)
    private BigDecimal royaltyAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @CreationTimestamp
    private Instant createdAt;

    public enum PaymentStatus {
        PENDING, PAID, OVERDUE
    }
}
