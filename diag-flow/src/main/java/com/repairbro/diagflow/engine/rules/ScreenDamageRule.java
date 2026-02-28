package com.repairbro.diagflow.engine.rules;

import com.repairbro.diagflow.engine.DiagnosticAction;
import com.repairbro.diagflow.engine.DiagnosticRule;
import com.repairbro.diagflow.engine.RepairContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScreenDamageRule implements DiagnosticRule {

    @Override
    public boolean matches(RepairContext ctx) {
        String symptom = (ctx.getSymptom() != null ? ctx.getSymptom() : "").toLowerCase();
        return symptom.contains("screen") || symptom.contains("display")
                || symptom.contains("cracked") || symptom.contains("dead pixel")
                || symptom.contains("flickering") || symptom.contains("blank screen")
                || symptom.contains("no display");
    }

    @Override
    public List<DiagnosticAction> evaluate(RepairContext ctx) {
        return List.of(
                DiagnosticAction.builder()
                        .stepOrder(1)
                        .instruction("Visual inspection: check for cracks, pressure marks, or liquid ingress on panel")
                        .expectedOutcome("DAMAGED/INTACT").confidenceWeight(0.20).build(),
                DiagnosticAction.builder()
                        .stepOrder(2).instruction("Connect external monitor/TV via HDMI to verify GPU output")
                        .expectedOutcome("DISPLAY_OK/NO_SIGNAL").confidenceWeight(0.25)
                        .notes("If external works, issue is panel/cable. If not, GPU/motherboard").build(),
                DiagnosticAction.builder()
                        .stepOrder(3).instruction("Inspect display cable (LVDS/eDP) for damage, reseat connector")
                        .expectedOutcome("PASS/FAIL").confidenceWeight(0.15).build(),
                DiagnosticAction.builder()
                        .stepOrder(4).instruction("Test with known-good replacement panel if available")
                        .expectedOutcome("PASS/FAIL").confidenceWeight(0.25)
                        .notes("Confirms panel vs cable vs board issue").build(),
                DiagnosticAction.builder()
                        .stepOrder(5).instruction("Check backlight fuse and inverter circuit on motherboard")
                        .expectedOutcome("PASS/FAIL").confidenceWeight(0.15)
                        .notes("Dim display usually indicates backlight circuit failure").build());
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public String ruleName() {
        return "Screen/Display Diagnosis";
    }
}
