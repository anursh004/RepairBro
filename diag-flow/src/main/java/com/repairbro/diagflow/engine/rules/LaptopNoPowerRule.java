package com.repairbro.diagflow.engine.rules;

import com.repairbro.diagflow.engine.DiagnosticAction;
import com.repairbro.diagflow.engine.DiagnosticRule;
import com.repairbro.diagflow.engine.RepairContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LaptopNoPowerRule implements DiagnosticRule {

    @Override
    public boolean matches(RepairContext ctx) {
        String symptom = (ctx.getSymptom() != null ? ctx.getSymptom() : "").toLowerCase();
        String device = (ctx.getDeviceType() != null ? ctx.getDeviceType() : "").toLowerCase();
        return (device.contains("laptop") || device.contains("desktop"))
                && (symptom.contains("not powering") || symptom.contains("no power")
                        || symptom.contains("won't turn on") || symptom.contains("dead")
                        || symptom.contains("not charging") || symptom.contains("won't start"));
    }

    @Override
    public List<DiagnosticAction> evaluate(RepairContext ctx) {
        return List.of(
                DiagnosticAction.builder()
                        .stepOrder(1)
                        .instruction("Check power adapter output with multimeter (expected 19V DC for most laptops)")
                        .expectedOutcome("PASS/FAIL").confidenceWeight(0.15)
                        .notes("If adapter is faulty, replace and retest").build(),
                DiagnosticAction.builder()
                        .stepOrder(2)
                        .instruction("Inspect battery health — check for swelling, test voltage at battery terminals")
                        .expectedOutcome("PASS/FAIL").confidenceWeight(0.15)
                        .notes("Swollen battery must be replaced immediately").build(),
                DiagnosticAction.builder()
                        .stepOrder(3).instruction("Test DC jack / charging port continuity")
                        .expectedOutcome("PASS/FAIL").confidenceWeight(0.10).build(),
                DiagnosticAction.builder()
                        .stepOrder(4)
                        .instruction("Remove RAM sticks, clean contacts, reseat and test with single stick")
                        .expectedOutcome("PASS/FAIL").confidenceWeight(0.10)
                        .notes("Test each RAM module individually in each slot").build(),
                DiagnosticAction.builder()
                        .stepOrder(5)
                        .instruction(
                                "Check motherboard for visible damage (burn marks, corroded traces, blown capacitors)")
                        .expectedOutcome("PASS/FAIL").confidenceWeight(0.20)
                        .notes("Board-level repair required if component damage found").build(),
                DiagnosticAction.builder()
                        .stepOrder(6).instruction("Measure standby voltages on power rail test points (3.3V, 5V, 12V)")
                        .expectedOutcome("PASS/FAIL").confidenceWeight(0.30)
                        .notes("Missing rails indicate MOSFET or IC failure — specialist repair needed").build());
    }

    @Override
    public int order() {
        return 10;
    }

    @Override
    public String ruleName() {
        return "Laptop/Desktop No Power Diagnosis";
    }
}
