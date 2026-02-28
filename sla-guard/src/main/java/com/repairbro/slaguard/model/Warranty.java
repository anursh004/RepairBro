package com.repairbro.slaguard.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Warranty issued after a completed repair.
 * Covers workmanship for 30-90 days depending on repair category.
 */
@Entity
@Table(name = "warranty")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Warranty {

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

    /** Category: QUICK_FIX (30d), STANDARD (60d), ADVANCED (90d) */
    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false)
    private int warrantyDays;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private WarrantyStatus status = WarrantyStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Claim details if warranty was used */
    @Column(columnDefinition = "TEXT")
    private String claimNotes;

    @CreationTimestamp
    private Instant createdAt;

    public enum WarrantyStatus {
        ACTIVE, EXPIRED, CLAIMED, VOIDED
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }

    public static Warranty createFor(UUID ticketId, UUID customerId, UUID branchId,
            String category, String description) {
        int days = switch (category.toUpperCase()) {
            case "QUICK_FIX" -> 30;
            case "STANDARD" -> 60;
            case "ADVANCED" -> 90;
            default -> 30;
        };
        Instant now = Instant.now();
        return Warranty.builder()
                .ticketId(ticketId).customerId(customerId).branchId(branchId)
                .category(category.toUpperCase())
                .warrantyDays(days)
                .startDate(now)
                .expiryDate(now.plus(days, ChronoUnit.DAYS))
                .description(description)
                .build();
    }
}
