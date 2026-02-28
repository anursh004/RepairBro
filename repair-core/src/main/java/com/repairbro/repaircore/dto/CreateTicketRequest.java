package com.repairbro.repaircore.dto;

import com.repairbro.repaircore.model.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateTicketRequest {

    @NotNull
    private UUID branchId;

    @NotNull
    private UUID customerId;

    @NotBlank
    private String deviceType;

    private String deviceModel;
    private String deviceSerial;

    @NotBlank
    private String symptom;

    private TicketPriority priority;
    private BigDecimal estimatedCost;
    private String notes;
}
