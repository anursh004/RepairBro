package com.repairbro.repairsim.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Persisted result of a simulation run — P&L, KPIs, and operational metrics per
 * branch.
 */
@Entity
@Table(name = "simulation_result")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SimulationResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private SimulationScenario scenario;

    /** Which simulated branch (0-indexed) */
    private int branchIndex;

    // ── Revenue & Costs ────────────────────────────
    private double totalRevenue;
    private double totalPartsCost;
    private double totalLaborCost;
    private double totalRent;
    private double totalRoyalty;
    private double netProfit;
    private double profitMarginPercent;

    // ── Operational KPIs ───────────────────────────
    private int totalTickets;
    private int completedTickets;
    private int slaBreach;
    private double firstTimeFixRate;
    private double meanRepairTimeHours;
    private double techUtilizationPercent;
    private int inventoryStockouts;

    // ── Break-even ─────────────────────────────────
    /** Minimum daily tickets needed to break even */
    private int breakEvenTicketsPerDay;

    @CreationTimestamp
    private Instant createdAt;
}
