package com.repairbro.fixbill.dto;

import com.repairbro.fixbill.model.InvoiceLineItem;
import com.repairbro.fixbill.model.InvoiceStatus;
import com.repairbro.fixbill.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {

    private UUID id;
    private String invoiceNumber;
    private UUID ticketId;
    private UUID branchId;
    private UUID customerId;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal taxRate;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private String notes;
    private List<LineItemDTO> lineItems;
    private List<PaymentDTO> payments;
    private Instant createdAt;
    private Instant paidAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItemDTO {
        private UUID id;
        private String description;
        private InvoiceLineItem.LineItemType type;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
        private UUID sparePartId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDTO {
        private UUID id;
        private BigDecimal amount;
        private Payment.PaymentMethod method;
        private Payment.PaymentStatus status;
        private String transactionId;
        private Instant createdAt;
        private Instant completedAt;
    }
}
