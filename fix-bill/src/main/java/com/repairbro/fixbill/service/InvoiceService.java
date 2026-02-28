package com.repairbro.fixbill.service;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.event.EventTopics;
import com.repairbro.commons.exception.RepairBroException;
import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.fixbill.dto.*;
import com.repairbro.fixbill.model.*;
import com.repairbro.fixbill.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    private static final AtomicLong invoiceCounter = new AtomicLong(1);

    @Transactional
    public InvoiceDTO createInvoice(CreateInvoiceRequest request) {
        // Check if invoice already exists for this ticket
        invoiceRepository.findByTicketId(request.getTicketId()).ifPresent(existing -> {
            throw new RepairBroException(
                    "Invoice already exists for ticket: " + request.getTicketId(),
                    HttpStatus.CONFLICT,
                    "INVOICE_EXISTS");
        });

        Invoice invoice = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .ticketId(request.getTicketId())
                .branchId(request.getBranchId())
                .customerId(request.getCustomerId())
                .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                .taxRate(request.getTaxRate() != null ? request.getTaxRate() : new BigDecimal("18.00"))
                .notes(request.getNotes())
                .subtotal(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .build();

        // Add line items
        for (CreateInvoiceRequest.LineItemRequest item : request.getLineItems()) {
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            InvoiceLineItem lineItem = InvoiceLineItem.builder()
                    .description(item.getDescription())
                    .type(item.getType())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .lineTotal(lineTotal)
                    .sparePartId(item.getSparePartId())
                    .build();
            invoice.addLineItem(lineItem);
        }

        invoice.recalculateTotals();
        invoice = invoiceRepository.save(invoice);

        log.info("Invoice created: {} for ticket {}", invoice.getInvoiceNumber(), invoice.getTicketId());
        publishEvent("InvoiceGenerated", invoice);

        return toDTO(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceDTO getInvoice(UUID invoiceId) {
        return toDTO(findOrThrow(invoiceId));
    }

    @Transactional(readOnly = true)
    public InvoiceDTO getByTicket(UUID ticketId) {
        Invoice invoice = invoiceRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "ticket:" + ticketId));
        return toDTO(invoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getByBranch(UUID branchId, Pageable pageable) {
        return invoiceRepository.findByBranchId(branchId, pageable).map(this::toDTO);
    }

    @Transactional
    public InvoiceDTO recordPayment(UUID invoiceId, RecordPaymentRequest request) {
        Invoice invoice = findOrThrow(invoiceId);

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RepairBroException("Invoice already fully paid", HttpStatus.BAD_REQUEST, "ALREADY_PAID");
        }

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(Payment.PaymentStatus.SUCCESS)
                .transactionId(request.getTransactionId())
                .gatewayRef(request.getGatewayRef())
                .completedAt(Instant.now())
                .build();

        invoice.getPayments().add(payment);

        // Check if fully paid
        BigDecimal totalPaid = invoice.getPayments().stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidAt(Instant.now());
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoice = invoiceRepository.save(invoice);
        log.info("Payment recorded for invoice {}: {} via {}", invoice.getInvoiceNumber(), request.getAmount(),
                request.getMethod());
        publishEvent("PaymentReceived", invoice);

        return toDTO(invoice);
    }

    // ── Helpers ───────────────────────────────────────────

    private Invoice findOrThrow(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId.toString()));
    }

    private String generateInvoiceNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "RB-" + date + "-" + String.format("%04d", invoiceCounter.getAndIncrement());
    }

    private void publishEvent(String type, Invoice invoice) {
        try {
            DomainEvent event = DomainEvent.builder()
                    .eventType("repairbro.fix-bill." + type)
                    .aggregateId(invoice.getId().toString())
                    .aggregateType("Invoice")
                    .source("fix-bill")
                    .payload(Map.of(
                            "invoiceId", invoice.getId().toString(),
                            "invoiceNumber", invoice.getInvoiceNumber(),
                            "ticketId", invoice.getTicketId().toString(),
                            "totalAmount", invoice.getTotalAmount().toString(),
                            "status", invoice.getStatus().name()))
                    .build();
            kafkaTemplate.send(EventTopics.BILLING_EVENTS, invoice.getId().toString(), event);
        } catch (Exception e) {
            log.warn("Failed to publish {} event: {}", type, e.getMessage());
        }
    }

    private InvoiceDTO toDTO(Invoice invoice) {
        return InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .ticketId(invoice.getTicketId())
                .branchId(invoice.getBranchId())
                .customerId(invoice.getCustomerId())
                .subtotal(invoice.getSubtotal())
                .taxAmount(invoice.getTaxAmount())
                .taxRate(invoice.getTaxRate())
                .discount(invoice.getDiscount())
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .notes(invoice.getNotes())
                .lineItems(invoice.getLineItems().stream()
                        .map(li -> InvoiceDTO.LineItemDTO.builder()
                                .id(li.getId())
                                .description(li.getDescription())
                                .type(li.getType())
                                .quantity(li.getQuantity())
                                .unitPrice(li.getUnitPrice())
                                .lineTotal(li.getLineTotal())
                                .sparePartId(li.getSparePartId())
                                .build())
                        .toList())
                .payments(invoice.getPayments().stream()
                        .map(p -> InvoiceDTO.PaymentDTO.builder()
                                .id(p.getId())
                                .amount(p.getAmount())
                                .method(p.getMethod())
                                .status(p.getStatus())
                                .transactionId(p.getTransactionId())
                                .createdAt(p.getCreatedAt())
                                .completedAt(p.getCompletedAt())
                                .build())
                        .toList())
                .createdAt(invoice.getCreatedAt())
                .paidAt(invoice.getPaidAt())
                .build();
    }
}
