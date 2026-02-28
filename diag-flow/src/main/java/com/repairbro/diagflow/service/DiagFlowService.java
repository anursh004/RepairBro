package com.repairbro.diagflow.service;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.event.EventTopics;
import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.diagflow.engine.*;
import com.repairbro.diagflow.model.*;
import com.repairbro.diagflow.repository.DiagFlowRepository;
import com.repairbro.diagflow.repository.DiagnosisEvaluationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiagFlowService {

    private final DiagFlowRepository diagFlowRepository;
    private final DiagnosisEvaluationRepository evaluationRepository;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final RuleEngine ruleEngine;

    @Transactional
    public DiagFlow createFlow(DiagFlow flow) {
        flow = diagFlowRepository.save(flow);
        log.info("Diagnostic flow created: {} [{}]", flow.getName(), flow.getId());
        return flow;
    }

    @Transactional(readOnly = true)
    public DiagFlow getFlow(UUID id) {
        return diagFlowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DiagFlow", id.toString()));
    }

    @Transactional(readOnly = true)
    public List<DiagFlow> getLibrary() {
        return diagFlowRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<DiagFlow> getFlowsByDevice(String deviceType) {
        return diagFlowRepository.findByDeviceTypeAndActiveTrue(deviceType);
    }

    /**
     * Evaluate a ticket using the real rule engine pipeline.
     * Runs all matching DiagnosticRule beans, aggregates confidence, and persists
     * the result.
     */
    @Transactional
    public DiagnosisEvaluation evaluate(UUID ticketId, String deviceType, String symptom) {
        // Build context for the rule engine
        RepairContext ctx = RepairContext.builder()
                .deviceType(deviceType)
                .symptom(symptom)
                .build();

        // Run rule engine pipeline
        DiagnosticResult result = ruleEngine.evaluate(ctx);

        // Also check for matching persisted flows
        List<DiagFlow> matchingFlows = diagFlowRepository.findByDeviceTypeAndActiveTrue(deviceType);
        DiagFlow bestMatch = matchingFlows.isEmpty() ? null : matchingFlows.get(0);

        // Build suggested actions string from rule engine results
        String suggestedActions = result.getRecommendedActions().stream()
                .map(a -> "Step " + a.getStepOrder() + ": " + a.getInstruction()
                        + (a.getNotes() != null ? " [" + a.getNotes() + "]" : ""))
                .collect(Collectors.joining("\n"));

        if (suggestedActions.isEmpty()) {
            suggestedActions = "No matching diagnostic rules found. Manual diagnosis required.";
        }

        // Determine status based on confidence
        DiagnosisEvaluation.EvalStatus status;
        if (result.getOverallConfidence() >= 0.7) {
            status = DiagnosisEvaluation.EvalStatus.IN_PROGRESS;
        } else if (result.getOverallConfidence() >= 0.3) {
            status = DiagnosisEvaluation.EvalStatus.IN_PROGRESS;
        } else {
            status = result.getMatchedRules().isEmpty()
                    ? DiagnosisEvaluation.EvalStatus.INCONCLUSIVE
                    : DiagnosisEvaluation.EvalStatus.PENDING;
        }

        DiagnosisEvaluation eval = DiagnosisEvaluation.builder()
                .ticketId(ticketId)
                .diagFlow(bestMatch)
                .confidenceScore(result.getOverallConfidence())
                .suggestedActions(suggestedActions)
                .rootCause(result.getSuggestedRootCause())
                .status(status)
                .build();

        eval = evaluationRepository.save(eval);
        log.info("Diagnosis evaluation for ticket {}: confidence={}, rules={}, actions={}",
                ticketId, eval.getConfidenceScore(), result.getMatchedRules().size(),
                result.getRecommendedActions().size());

        publishEvent("DiagnosisCompleted", eval);
        return eval;
    }

    private void publishEvent(String type, DiagnosisEvaluation eval) {
        try {
            DomainEvent event = DomainEvent.builder()
                    .eventType("repairbro.diag-flow." + type)
                    .aggregateId(eval.getId().toString())
                    .aggregateType("DiagnosisEvaluation")
                    .source("diag-flow")
                    .payload(Map.of(
                            "ticketId", eval.getTicketId().toString(),
                            "confidence", String.valueOf(eval.getConfidenceScore()),
                            "status", eval.getStatus().name(),
                            "rootCause", eval.getRootCause() != null ? eval.getRootCause() : ""))
                    .build();
            kafkaTemplate.send(EventTopics.DIAGNOSIS_EVENTS, eval.getTicketId().toString(), event);
        } catch (Exception e) {
            log.warn("Failed to publish {} event: {}", type, e.getMessage());
        }
    }
}
