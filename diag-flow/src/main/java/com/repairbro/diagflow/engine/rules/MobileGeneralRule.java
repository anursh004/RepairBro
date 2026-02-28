package com.repairbro.diagflow.engine.rules;

import com.repairbro.diagflow.engine.DiagnosticAction;
import com.repairbro.diagflow.engine.DiagnosticRule;
import com.repairbro.diagflow.engine.RepairContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MobileGeneralRule implements DiagnosticRule {

    @Override
    public boolean matches(RepairContext ctx) {
        String device = (ctx.getDeviceType() != null ? ctx.getDeviceType() : "").toLowerCase();
        return device.contains("mobile") || device.contains("phone")
                || device.contains("iphone") || device.contains("samsung")
                || device.contains("android");
    }

    @Override
    public List<DiagnosticAction> evaluate(RepairContext ctx) {
        String symptom = (ctx.getSymptom() != null ? ctx.getSymptom() : "").toLowerCase();

        // Base steps applicable to all mobile issues
        var actions = new java.util.ArrayList<>(List.of(
                DiagnosticAction.builder()
                        .stepOrder(1)
                        .instruction(
                                "Visual inspection: check for screen cracks, dents, port damage, button responsiveness")
                        .expectedOutcome("DAMAGE_REPORT").confidenceWeight(0.15).build(),
                DiagnosticAction.builder()
                        .stepOrder(2).instruction("Check charging with known-good cable and adapter")
                        .expectedOutcome("CHARGES/NO_CHARGE").confidenceWeight(0.15).build(),
                DiagnosticAction.builder()
                        .stepOrder(3).instruction("Attempt force restart (device-specific button combo)")
                        .expectedOutcome("BOOTS/NO_BOOT").confidenceWeight(0.15).build()));

        if (symptom.contains("screen") || symptom.contains("display")) {
            actions.add(DiagnosticAction.builder()
                    .stepOrder(4).instruction("Test touch digitizer response across all screen zones")
                    .expectedOutcome("RESPONSIVE/DEAD_ZONES").confidenceWeight(0.20).build());
        }
        if (symptom.contains("speaker") || symptom.contains("sound") || symptom.contains("microphone")) {
            actions.add(DiagnosticAction.builder()
                    .stepOrder(4).instruction("Test earpiece, loudspeaker, and microphone with test call")
                    .expectedOutcome("PASS/FAIL").confidenceWeight(0.20).build());
        }

        actions.add(DiagnosticAction.builder()
                .stepOrder(5).instruction("Run built-in hardware diagnostics (Samsung Members / Apple Diagnostics)")
                .expectedOutcome("REPORT").confidenceWeight(0.20)
                .notes("Document all failed components for parts order").build());

        return actions;
    }

    @Override
    public int order() {
        return 50;
    }

    @Override
    public String ruleName() {
        return "Mobile/Phone General Diagnosis";
    }
}
