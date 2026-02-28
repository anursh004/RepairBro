package com.repairbro.franchisehub.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "franchise")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Franchise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private UUID branchId;

    @Column(nullable = false, length = 128)
    private String ownerName;

    @Column(length = 128)
    private String businessName;

    @Column(length = 128)
    private String email;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private FranchiseTier tier = FranchiseTier.STANDARD;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private FranchiseStatus status = FranchiseStatus.PENDING;

    @Column(precision = 12, scale = 2)
    private BigDecimal setupFee;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal royaltyPercent = new BigDecimal("7.00");

    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    public enum FranchiseTier {
        STANDARD, PREMIUM
    }

    public enum FranchiseStatus {
        PENDING, ACTIVE, SUSPENDED, TERMINATED
    }
}
