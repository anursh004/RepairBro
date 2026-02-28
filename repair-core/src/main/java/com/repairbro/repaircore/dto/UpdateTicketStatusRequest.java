package com.repairbro.repaircore.dto;

import com.repairbro.repaircore.model.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateTicketStatusRequest {

    @NotNull
    private TicketStatus status;

    private UUID performedBy;
    private String notes;
    private BigDecimal finalCost;
}
