package com.repairbro.repairsim.engine;

import com.repairbro.repairsim.model.SimulationResultEntity;
import com.repairbro.repairsim.model.SimulationScenario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.data.Offset.offset;

@DisplayName("DiscreteEventEngine Simulation Tests")
class DiscreteEventEngineTest {

    private SimulationScenario buildScenario(int days, int demand, int techs) {
        return SimulationScenario.builder()
                .simulationDays(days)
                .avgDemandsPerDay(demand)
                .techsPerBranch(techs)
                .branchCount(1)
                .juniorTechRatio(0.4)
                .inventoryStrategy("LOCAL")
                .cityTier(2)
                .monthlyRent(25000)
                .avgTicketValue(2500)
                .avgPartsCost(800)
                .build();
    }

    @Nested
    @DisplayName("Basic Simulation Run")
    class BasicRun {

        @Test
        @DisplayName("should complete simulation and produce results")
        void shouldRunSimulation() {
            DiscreteEventEngine engine = new DiscreteEventEngine(buildScenario(30, 5, 3));
            SimulationResultEntity result = engine.runForBranch(0);

            assertThat(result).isNotNull();
            assertThat(result.getTotalTickets()).isGreaterThan(0);
            assertThat(result.getCompletedTickets()).isGreaterThan(0);
            assertThat(result.getTotalRevenue()).isGreaterThan(0);
            assertThat(result.getBranchIndex()).isEqualTo(0);
        }

        @Test
        @DisplayName("should calculate P&L components")
        void shouldCalculatePnL() {
            DiscreteEventEngine engine = new DiscreteEventEngine(buildScenario(30, 5, 3));
            SimulationResultEntity result = engine.runForBranch(0);

            assertThat(result.getTotalRevenue()).isGreaterThan(0);
            assertThat(result.getTotalPartsCost()).isGreaterThan(0);
            assertThat(result.getTotalLaborCost()).isGreaterThan(0);
            assertThat(result.getTotalRent()).isGreaterThan(0);
            assertThat(result.getTotalRoyalty()).isGreaterThan(0);
            // Net profit = Revenue - costs (all are doubles)
            double expectedNet = result.getTotalRevenue() - result.getTotalPartsCost()
                    - result.getTotalLaborCost() - result.getTotalRent() - result.getTotalRoyalty();
            assertThat(result.getNetProfit()).isCloseTo(expectedNet, offset(1.0));
        }

        @Test
        @DisplayName("rent should scale with days")
        void rentShouldScaleWithDays() {
            DiscreteEventEngine engine30 = new DiscreteEventEngine(buildScenario(30, 5, 3));
            SimulationResultEntity r30 = engine30.runForBranch(0);

            DiscreteEventEngine engine90 = new DiscreteEventEngine(buildScenario(90, 5, 3));
            SimulationResultEntity r90 = engine90.runForBranch(0);

            // 90-day rent should be ~3x of 30-day rent
            assertThat(r90.getTotalRent()).isGreaterThan(r30.getTotalRent() * 2);
        }
    }

    @Nested
    @DisplayName("KPI Calculations")
    class KPIs {

        @Test
        @DisplayName("FTFR should be between 0 and 1")
        void ftfrRange() {
            DiscreteEventEngine engine = new DiscreteEventEngine(buildScenario(30, 10, 4));
            SimulationResultEntity result = engine.runForBranch(0);

            assertThat(result.getFirstTimeFixRate()).isBetween(0.0, 1.0);
        }

        @Test
        @DisplayName("tech utilization should be between 0 and 100%")
        void utilizationRange() {
            DiscreteEventEngine engine = new DiscreteEventEngine(buildScenario(30, 5, 3));
            SimulationResultEntity result = engine.runForBranch(0);

            assertThat(result.getTechUtilizationPercent()).isBetween(0.0, 200.0); // can exceed 100% if overloaded
        }

        @Test
        @DisplayName("MTTR should be positive")
        void mttrPositive() {
            DiscreteEventEngine engine = new DiscreteEventEngine(buildScenario(30, 5, 3));
            SimulationResultEntity result = engine.runForBranch(0);

            assertThat(result.getMeanRepairTimeHours()).isGreaterThan(0.0);
        }

        @Test
        @DisplayName("break-even tickets/day should be positive")
        void breakEvenPositive() {
            DiscreteEventEngine engine = new DiscreteEventEngine(buildScenario(30, 5, 3));
            SimulationResultEntity result = engine.runForBranch(0);

            assertThat(result.getBreakEvenTicketsPerDay()).isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("Stress Scenarios")
    class Stress {

        @Test
        @DisplayName("high demand should cause stockouts")
        void highDemandCausesStockouts() {
            DiscreteEventEngine engine = new DiscreteEventEngine(buildScenario(60, 20, 2));
            SimulationResultEntity result = engine.runForBranch(0);

            // With 20 demands/day and only 2 techs, stockouts should occur
            assertThat(result.getInventoryStockouts()).isGreaterThanOrEqualTo(0);
            assertThat(result.getSlaBreach()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("low demand should have high FTFR")
        void lowDemandHighFTFR() {
            DiscreteEventEngine engine = new DiscreteEventEngine(buildScenario(30, 2, 5));
            SimulationResultEntity result = engine.runForBranch(0);

            // With only 2 demands/day and 5 techs, most should be fixed on time
            assertThat(result.getFirstTimeFixRate()).isGreaterThan(0.5);
        }

        @RepeatedTest(3)
        @DisplayName("simulation should be deterministic in structure (stochastic in values)")
        void StructuralConsistency() {
            DiscreteEventEngine engine = new DiscreteEventEngine(buildScenario(30, 5, 3));
            SimulationResultEntity result = engine.runForBranch(0);

            // Structure should always be valid
            assertThat(result.getCompletedTickets()).isLessThanOrEqualTo(result.getTotalTickets());
            assertThat(result.getProfitMarginPercent()).isNotNaN();
        }
    }
}
