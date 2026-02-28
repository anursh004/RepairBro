package com.repairbro.fixbill.dto;

import com.repairbro.fixbill.model.InvoiceLineItem;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreateInvoiceRequest {

    @NotNull
    private UUID ticketId;

    @NotNull
    private UUID branchId;

    @NotNull
    private UUID customerId;

    private BigDecimal discount;
    private BigDecimal taxRate;
    private String notes;

    @NotEmpty
    private List<LineItemRequest> lineItems;

    @Data
    public static class LineItemRequest {
        private String description;
        private InvoiceLineItem.LineItemType type;
        private int quantity;
        private BigDecimal unitPrice;
        private UUID sparePartId;
    }
}
