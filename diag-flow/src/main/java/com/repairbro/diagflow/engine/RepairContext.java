package com.repairbro.diagflow.engine;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Context object passed to diagnostic rules for evaluation.
 * Contains all information about the device and reported symptoms.
 */
@Data
@Builder
public class RepairContext {
    private String deviceType; // e.g. "LAPTOP", "MOBILE", "DRONE"
    private String deviceModel; // e.g. "MacBook Air M2"
    private String symptom; // Primary symptom description
    private String symptomCategory; // e.g. "power", "display", "battery", "water_damage"
    private Map<String, String> additionalInfo; // Any extra context
}
