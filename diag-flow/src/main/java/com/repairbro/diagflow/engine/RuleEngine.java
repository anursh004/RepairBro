package com.repairbro.diagflow.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Rule engine that evaluates all registered DiagnosticRule beans in priority
 * order.
 * Aggregates actions from all matching rules and computes an overall confidence
 * score.
 */
@Slf4j
@Component
public class RuleEngine {

    private final List<DiagnosticRule> rules;

    public RuleEngine(List<DiagnosticRule> rules) {
        this.rules = rules.stream()
                .sorted(Comparator.comparingInt(DiagnosticRule::order))
                .toList();
        log.info("DiagnosticRule engine initialized with {} rules: {}",
                rules.size(), rules.stream().map(DiagnosticRule::ruleName).toList());
    }

    /**
     * Run all matching rules against the given context.
     * Actions from multiple matching rules are merged and reordered.
     */
    public DiagnosticResult evaluate(RepairContext ctx) {
        List<DiagnosticAction> allActions = new ArrayList<>();
        List<String> matchedRules = new ArrayList<>();
        double totalConfidence = 0.0;

        for (DiagnosticRule rule : rules) {
            if (rule.matches(ctx)) {
                log.info("Rule matched: {} (order={})", rule.ruleName(), rule.order());
                matchedRules.add(rule.ruleName());

                List<DiagnosticAction> actions = rule.evaluate(ctx);
                allActions.addAll(actions);

                // Aggregate confidence from this rule's actions
                double ruleConfidence = actions.stream()
                        .mapToDouble(DiagnosticAction::getConfidenceWeight)
                        .sum();
                totalConfidence += ruleConfidence;
            }
        }

        // Normalize confidence to 0.0 - 1.0 range
        double normalizedConfidence = Math.min(totalConfidence / Math.max(matchedRules.size(), 1), 1.0);

        // Reorder all actions sequentially
        for (int i = 0; i < allActions.size(); i++) {
            allActions.get(i).setStepOrder(i + 1);
        }

        String rootCause = deriveRootCause(matchedRules, ctx);

        DiagnosticResult result = DiagnosticResult.builder()
                .overallConfidence(Math.round(normalizedConfidence * 100.0) / 100.0)
                .recommendedActions(allActions)
                .matchedRules(matchedRules)
                .suggestedRootCause(rootCause)
                .build();

        log.info("Diagnosis complete: {} rules matched, {} actions, confidence={}",
                matchedRules.size(), allActions.size(), result.getOverallConfidence());
        return result;
    }

    private String deriveRootCause(List<String> matchedRules, RepairContext ctx) {
        if (matchedRules.isEmpty()) {
            return "Unable to determine root cause — manual diagnosis required";
        }
        if (matchedRules.size() == 1) {
            return "Likely cause aligned with: " + matchedRules.get(0);
        }
        return "Multiple potential causes identified: " + String.join(", ", matchedRules)
                + ". Follow diagnostic steps to isolate.";
    }
}
