package com.repairbro.fixbill.dto;

import com.repairbro.fixbill.model.Payment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecordPaymentRequest {

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private Payment.PaymentMethod method;

    private String transactionId;
    private String gatewayRef;
}
