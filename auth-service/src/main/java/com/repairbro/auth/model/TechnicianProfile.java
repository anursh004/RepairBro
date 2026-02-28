package com.repairbro.auth.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Extended profile for users with TECH role.
 * Tracks skill level, certifications, specializations, and utilization.
 */
@Entity
@Table(name = "technician_profile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TechnicianProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    /** Links to the users table */
    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private UUID branchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SkillLevel skillLevel = SkillLevel.JUNIOR;

    /** Comma-separated specializations: e.g. "laptop,drone,mobile" */
    @Column(columnDefinition = "TEXT")
    private String specializations;

    /** Comma-separated certifications: e.g. "Apple Certified,CompTIA A+" */
    @Column(columnDefinition = "TEXT")
    private String certifications;

    /** Hourly rate for cost calculations */
    @Column(precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal hourlyRate = BigDecimal.ZERO;

    /** Total tickets resolved */
    @Builder.Default
    private int ticketsResolved = 0;

    /** Average repair time in hours */
    @Builder.Default
    private double avgRepairTimeHours = 0.0;

    /** First-Time Fix Rate (0.0 to 1.0) */
    @Builder.Default
    private double firstTimeFixRate = 0.0;

    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
