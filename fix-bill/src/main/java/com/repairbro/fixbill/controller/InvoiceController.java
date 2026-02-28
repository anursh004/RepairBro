package com.repairbro.fixbill.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.fixbill.dto.*;
import com.repairbro.fixbill.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/invoices")
    @PreAuthorize("hasAuthority('PERM_INVOICE_CREATE')")
    public ResponseEntity<ApiResponse<InvoiceDTO>> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceDTO invoice = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(invoice, "Invoice created"));
    }

    @GetMapping("/invoices/{invoiceId}")
    @PreAuthorize("hasAuthority('PERM_INVOICE_VIEW')")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoice(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(ApiResponse.ok(invoiceService.getInvoice(invoiceId)));
    }

    @GetMapping("/invoices/ticket/{ticketId}")
    @PreAuthorize("hasAuthority('PERM_INVOICE_VIEW')")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getByTicket(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(ApiResponse.ok(invoiceService.getByTicket(ticketId)));
    }

    @GetMapping("/invoices/branch/{branchId}")
    @PreAuthorize("hasAuthority('PERM_INVOICE_VIEW')")
    public ResponseEntity<ApiResponse<Page<InvoiceDTO>>> getByBranch(
            @PathVariable UUID branchId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(invoiceService.getByBranch(branchId, pageable)));
    }

    @PostMapping("/invoices/{invoiceId}/payments")
    @PreAuthorize("hasAuthority('PERM_INVOICE_PAYMENT')")
    public ResponseEntity<ApiResponse<InvoiceDTO>> recordPayment(
            @PathVariable UUID invoiceId,
            @Valid @RequestBody RecordPaymentRequest request) {
        InvoiceDTO invoice = invoiceService.recordPayment(invoiceId, request);
        return ResponseEntity.ok(ApiResponse.ok(invoice, "Payment recorded"));
    }
}
