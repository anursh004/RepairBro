package com.repairbro.diagflow.engine;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Aggregated result from running the diagnostic rule pipeline.
 */
@Data
@Builder
public class DiagnosticResult {
    private double overallConfidence;
    private List<DiagnosticAction> recommendedActions;
    private List<String> matchedRules;
    private String suggestedRootCause;
}
