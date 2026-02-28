package com.repairbro.auth.dto;

import com.repairbro.auth.model.SkillLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TechnicianDTO {
    private UUID id;
    private UUID userId;
    private String fullName;
    private String email;
    private UUID branchId;
    private SkillLevel skillLevel;
    private String specializations;
    private String certifications;
    private BigDecimal hourlyRate;
    private int ticketsResolved;
    private double avgRepairTimeHours;
    private double firstTimeFixRate;
    private boolean active;
    private Instant createdAt;
}
