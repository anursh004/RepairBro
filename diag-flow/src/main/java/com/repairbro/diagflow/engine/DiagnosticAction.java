package com.repairbro.diagflow.engine;

import lombok.Builder;
import lombok.Data;

/**
 * A single diagnostic action recommended by a rule.
 */
@Data
@Builder
public class DiagnosticAction {
    private int stepOrder;
    private String instruction;
    private String expectedOutcome;
    private double confidenceWeight; // 0.0 to 1.0
    private String notes;
}
