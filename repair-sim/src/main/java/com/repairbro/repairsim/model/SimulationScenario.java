package com.repairbro.repairsim.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Simulation scenario configuration — defines inputs for a discrete-event
 * simulation run.
 */
@Entity
@Table(name = "simulation_scenario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SimulationScenario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, length = 128)
    private String name;

    /** Number of branches to simulate */
    private int branchCount;

    /** Average repairs per day per branch */
    private int avgDemandsPerDay;

    /** Simulation duration in days */
    private int simulationDays;

    /** Fraction of junior techs (0.0-1.0) */
    @Builder.Default
    private double juniorTechRatio = 0.6;

    /** Total technicians per branch */
    @Builder.Default
    private int techsPerBranch = 3;

    /** Inventory strategy: CENTRALIZED or LOCAL */
    @Column(length = 20)
    @Builder.Default
    private String inventoryStrategy = "LOCAL";

    /** City tier (affects rent and demand) */
    @Builder.Default
    private int cityTier = 2;

    /** Monthly rent per branch */
    @Builder.Default
    private double monthlyRent = 50000;

    /** Average ticket value in ₹ */
    @Builder.Default
    private double avgTicketValue = 1800;

    /** Average parts cost per repair in ₹ */
    @Builder.Default
    private double avgPartsCost = 500;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private ScenarioStatus status = ScenarioStatus.CREATED;

    @CreationTimestamp
    private Instant createdAt;

    public enum ScenarioStatus {
        CREATED, RUNNING, COMPLETED, FAILED
    }
}
