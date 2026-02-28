package com.repairbro.diagflow.engine.rules;

import com.repairbro.diagflow.engine.DiagnosticAction;
import com.repairbro.diagflow.engine.DiagnosticRule;
import com.repairbro.diagflow.engine.RepairContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WaterDamageRule implements DiagnosticRule {

    @Override
    public boolean matches(RepairContext ctx) {
        String symptom = (ctx.getSymptom() != null ? ctx.getSymptom() : "").toLowerCase();
        return symptom.contains("water") || symptom.contains("liquid")
                || symptom.contains("spill") || symptom.contains("wet")
                || symptom.contains("corrosion");
    }

    @Override
    public List<DiagnosticAction> evaluate(RepairContext ctx) {
        return List.of(
                DiagnosticAction.builder()
                        .stepOrder(1).instruction("IMMEDIATE: Power off device, remove battery if possible")
                        .expectedOutcome("DONE").confidenceWeight(0.10)
                        .notes("Prevent further short-circuit damage").build(),
                DiagnosticAction.builder()
                        .stepOrder(2)
                        .instruction(
                                "Disassemble and inspect all boards under magnification for corrosion/mineral deposits")
                        .expectedOutcome("CORROSION_FOUND/CLEAN").confidenceWeight(0.20).build(),
                DiagnosticAction.builder()
                        .stepOrder(3).instruction("Clean affected areas with 99% isopropyl alcohol and soft brush")
                        .expectedOutcome("CLEANED").confidenceWeight(0.15)
                        .notes("Use ultrasonic cleaner for thorough cleaning if available").build(),
                DiagnosticAction.builder()
                        .stepOrder(4).instruction("Check liquid contact indicators (LCIs) to confirm water ingress")
                        .expectedOutcome("TRIGGERED/CLEAR").confidenceWeight(0.10).build(),
                DiagnosticAction.builder()
                        .stepOrder(5)
                        .instruction("After cleaning and drying (24h minimum), reassemble and test all functions")
                        .expectedOutcome("PASS/PARTIAL/FAIL").confidenceWeight(0.25).build(),
                DiagnosticAction.builder()
                        .stepOrder(6).instruction("Test all ports, speakers, keyboard, trackpad, camera individually")
                        .expectedOutcome("PASS/FAIL_LIST").confidenceWeight(0.20)
                        .notes("Water damage often affects specific subsystems — document each").build());
    }

    @Override
    public int order() {
        return 15;
    }

    @Override
    public String ruleName() {
        return "Water/Liquid Damage Diagnosis";
    }
}
