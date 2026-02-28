package com.repairbro.diagflow.engine;

import java.util.List;

/**
 * Plugin-style interface for diagnostic rules.
 * Each rule checks whether it applies to the given device/symptom context,
 * and if so, returns a list of diagnostic actions to follow.
 * Rules are registered as Spring beans and evaluated in pipeline order.
 */
public interface DiagnosticRule {

    /**
     * @return true if this rule is relevant for the given context.
     */
    boolean matches(RepairContext ctx);

    /**
     * Evaluate the context and return ordered diagnostic steps.
     * Only called if {@link #matches(RepairContext)} returns true.
     */
    List<DiagnosticAction> evaluate(RepairContext ctx);

    /**
     * Priority order — lower value = evaluated first.
     */
    default int order() {
        return 100;
    }

    /**
     * Human-readable name of this rule.
     */
    String ruleName();
}
