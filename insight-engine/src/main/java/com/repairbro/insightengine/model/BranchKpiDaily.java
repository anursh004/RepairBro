package com.repairbro.insightengine.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Pre-aggregated KPI snapshot per branch per day.
 * Populated by consuming domain events from all services.
 */
@Entity
@Table(name = "branch_kpi_daily")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BranchKpiDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private UUID branchId;
    @Column(nullable = false)
    private LocalDate date;

    private int ticketsCreated;
    private int ticketsCompleted;
    private int slBreaches;

    @Column(precision = 12, scale = 2)
    private BigDecimal revenue;

    /** Mean time to repair in hours */
    private double mttrHours;

    /** First time fix rate (0.0 - 1.0) */
    private double ftfr;

    /** Technician utilization percentage */
    private double techUtilization;

    @CreationTimestamp
    private Instant createdAt;
}
