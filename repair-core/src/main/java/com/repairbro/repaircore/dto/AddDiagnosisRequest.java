package com.repairbro.repaircore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class AddDiagnosisRequest {

    @NotBlank
    private String name;

    private String result;
    private UUID performedBy;
    private String notes;
}
