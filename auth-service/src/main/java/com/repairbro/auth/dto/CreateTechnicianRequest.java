package com.repairbro.auth.dto;

import com.repairbro.auth.model.SkillLevel;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateTechnicianRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Branch ID is required")
    private UUID branchId;

    private SkillLevel skillLevel = SkillLevel.JUNIOR;

    /** Comma-separated: "laptop,mobile,drone" */
    private String specializations;

    /** Comma-separated: "Apple Certified,CompTIA A+" */
    private String certifications;

    private BigDecimal hourlyRate;
}
