package com.repairbro.diagflow.engine.rules;

import com.repairbro.diagflow.engine.DiagnosticAction;
import com.repairbro.diagflow.engine.DiagnosticRule;
import com.repairbro.diagflow.engine.RepairContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatteryDrainRule implements DiagnosticRule {

    @Override
    public boolean matches(RepairContext ctx) {
        String symptom = (ctx.getSymptom() != null ? ctx.getSymptom() : "").toLowerCase();
        return symptom.contains("battery") || symptom.contains("drain")
                || symptom.contains("charge") || symptom.contains("overheating")
                || symptom.contains("swollen") || symptom.contains("bulging");
    }

    @Override
    public List<DiagnosticAction> evaluate(RepairContext ctx) {
        return List.of(
                DiagnosticAction.builder()
                        .stepOrder(1).instruction("Check battery health via OS diagnostic (coconutBattery/HWInfo)")
                        .expectedOutcome("CYCLE_COUNT/WEAR_LEVEL").confidenceWeight(0.20)
                        .notes("Battery above 80% wear or >500 cycles needs replacement").build(),
                DiagnosticAction.builder()
                        .stepOrder(2).instruction("Visually inspect battery for swelling or deformation")
                        .expectedOutcome("NORMAL/SWOLLEN").confidenceWeight(0.25)
                        .notes("Swollen battery is a safety hazard — dispose per protocol").build(),
                DiagnosticAction.builder()
                        .stepOrder(3).instruction("Measure battery voltage with multimeter (compare to rated voltage)")
                        .expectedOutcome("PASS/FAIL").confidenceWeight(0.15).build(),
                DiagnosticAction.builder()
                        .stepOrder(4).instruction("Check for high CPU/background processes causing abnormal drain")
                        .expectedOutcome("NORMAL/HIGH_USAGE").confidenceWeight(0.15)
                        .notes("Malware or stuck processes can cause rapid drain").build(),
                DiagnosticAction.builder()
                        .stepOrder(5).instruction("Test with replacement battery if available")
                        .expectedOutcome("PASS/FAIL").confidenceWeight(0.25).build());
    }

    @Override
    public int order() {
        return 30;
    }

    @Override
    public String ruleName() {
        return "Battery Health & Drain Diagnosis";
    }
}
