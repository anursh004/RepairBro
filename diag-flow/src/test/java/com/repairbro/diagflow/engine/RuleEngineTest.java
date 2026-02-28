package com.repairbro.diagflow.engine;

import com.repairbro.diagflow.engine.rules.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RuleEngine + Diagnostic Rules Unit Tests")
class RuleEngineTest {

    private RuleEngine engine;

    @BeforeEach
    void setUp() {
        // Wire all 5 rules — same as Spring would
        engine = new RuleEngine(List.of(
                new LaptopNoPowerRule(),
                new ScreenDamageRule(),
                new BatteryDrainRule(),
                new WaterDamageRule(),
                new MobileGeneralRule()));
    }

    @Nested
    @DisplayName("Laptop No Power Scenario")
    class LaptopNoPower {

        @Test
        @DisplayName("should match laptop with 'not powering on' symptom")
        void shouldMatchLaptopNoPower() {
            RepairContext ctx = RepairContext.builder()
                    .deviceType("LAPTOP").deviceModel("MacBook Air M2")
                    .symptom("Not powering on, no LED activity")
                    .build();

            DiagnosticResult result = engine.evaluate(ctx);

            assertThat(result.getMatchedRules()).contains("Laptop/Desktop No Power Diagnosis");
            assertThat(result.getRecommendedActions()).isNotEmpty();
            assertThat(result.getOverallConfidence()).isGreaterThan(0.0);
            assertThat(result.getSuggestedRootCause()).isNotBlank();
        }

        @Test
        @DisplayName("should generate multiple diagnostic steps")
        void shouldHaveMultipleSteps() {
            RepairContext ctx = RepairContext.builder()
                    .deviceType("LAPTOP")
                    .symptom("dead, won't turn on")
                    .build();

            DiagnosticResult result = engine.evaluate(ctx);
            assertThat(result.getRecommendedActions().size()).isGreaterThanOrEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Screen Damage Scenario")
    class ScreenDamage {

        @Test
        @DisplayName("should match any device with screen/display symptom")
        void shouldMatchScreenIssue() {
            RepairContext ctx = RepairContext.builder()
                    .deviceType("MOBILE").deviceModel("iPhone 15")
                    .symptom("Cracked screen after drop")
                    .build();

            DiagnosticResult result = engine.evaluate(ctx);

            assertThat(result.getMatchedRules()).contains("Screen/Display Diagnosis");
            // Mobile should also match MobileGeneralRule
            assertThat(result.getMatchedRules()).contains("Mobile/Phone General Diagnosis");
        }
    }

    @Nested
    @DisplayName("Water Damage Scenario")
    class WaterDamage {

        @Test
        @DisplayName("should match water/liquid/spill symptoms")
        void shouldMatchWaterDamage() {
            RepairContext ctx = RepairContext.builder()
                    .deviceType("LAPTOP")
                    .symptom("Water spill on keyboard, not responding")
                    .build();

            DiagnosticResult result = engine.evaluate(ctx);

            assertThat(result.getMatchedRules()).contains("Water/Liquid Damage Diagnosis");
            assertThat(result.getOverallConfidence()).isGreaterThan(0.3);
        }

        @Test
        @DisplayName("first action should be safety-related for water damage")
        void shouldPrioritizeSafety() {
            RepairContext ctx = RepairContext.builder()
                    .deviceType("MOBILE")
                    .symptom("liquid damage, phone died")
                    .build();

            DiagnosticResult result = engine.evaluate(ctx);
            // Water damage rule has higher priority (order=15) than Mobile (order=50)
            // After step reordering, first action should contain "power off" from
            // WaterDamageRule
            assertThat(result.getRecommendedActions().get(0).getInstruction())
                    .containsIgnoringCase("power off");
        }
    }

    @Nested
    @DisplayName("Battery Issues")
    class BatteryIssues {

        @Test
        @DisplayName("should match battery drain/swollen symptoms")
        void shouldMatchBattery() {
            RepairContext ctx = RepairContext.builder()
                    .deviceType("LAPTOP")
                    .symptom("Battery swollen, trackpad bulging")
                    .build();

            DiagnosticResult result = engine.evaluate(ctx);
            assertThat(result.getMatchedRules()).contains("Battery Health & Drain Diagnosis");
        }
    }

    @Nested
    @DisplayName("Mobile General Scenario")
    class MobileGeneral {

        @Test
        @DisplayName("should match any mobile device")
        void shouldMatchMobile() {
            RepairContext ctx = RepairContext.builder()
                    .deviceType("MOBILE").deviceModel("Samsung Galaxy S24")
                    .symptom("Speaker not working")
                    .build();

            DiagnosticResult result = engine.evaluate(ctx);
            assertThat(result.getMatchedRules()).contains("Mobile/Phone General Diagnosis");
        }

        @Test
        @DisplayName("should NOT match desktop")
        void shouldNotMatchDesktop() {
            RepairContext ctx = RepairContext.builder()
                    .deviceType("DESKTOP")
                    .symptom("Blue screen crashes")
                    .build();

            DiagnosticResult result = engine.evaluate(ctx);
            assertThat(result.getMatchedRules()).doesNotContain("Mobile/Phone General Diagnosis");
        }
    }

    @Nested
    @DisplayName("Multiple Rules Matching")
    class MultipleRules {

        @Test
        @DisplayName("should combine actions from multiple matching rules")
        void shouldCombineRules() {
            RepairContext ctx = RepairContext.builder()
                    .deviceType("MOBILE")
                    .symptom("battery drain and cracked screen")
                    .build();

            DiagnosticResult result = engine.evaluate(ctx);

            // Should match BatteryDrainRule + ScreenDamageRule + MobileGeneralRule
            assertThat(result.getMatchedRules().size()).isGreaterThanOrEqualTo(2);
            assertThat(result.getSuggestedRootCause()).contains("Multiple potential causes");
        }

        @Test
        @DisplayName("should reorder steps sequentially across all rules")
        void shouldReorderSteps() {
            RepairContext ctx = RepairContext.builder()
                    .deviceType("MOBILE")
                    .symptom("battery drain and screen cracked")
                    .build();

            DiagnosticResult result = engine.evaluate(ctx);
            var actions = result.getRecommendedActions();

            // Steps should be numbered 1, 2, 3...
            for (int i = 0; i < actions.size(); i++) {
                assertThat(actions.get(i).getStepOrder()).isEqualTo(i + 1);
            }
        }
    }

    @Nested
    @DisplayName("No Match Scenario")
    class NoMatch {

        @Test
        @DisplayName("should return empty result for unknown device/symptom")
        void shouldReturnEmptyForNoMatch() {
            RepairContext ctx = RepairContext.builder()
                    .deviceType("PRINTER")
                    .symptom("paper jam")
                    .build();

            DiagnosticResult result = engine.evaluate(ctx);
            assertThat(result.getMatchedRules()).isEmpty();
            assertThat(result.getSuggestedRootCause()).contains("manual diagnosis required");
        }
    }
}
