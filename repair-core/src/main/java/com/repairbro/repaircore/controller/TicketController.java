package com.repairbro.repaircore.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.repaircore.dto.*;
import com.repairbro.repaircore.model.TicketStatus;
import com.repairbro.repaircore.service.TicketService;
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
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_TICKET_CREATE')")
    public ResponseEntity<ApiResponse<TicketDTO>> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        TicketDTO ticket = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(ticket, "Ticket created successfully"));
    }

    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAuthority('PERM_TICKET_VIEW')")
    public ResponseEntity<ApiResponse<TicketDTO>> getTicket(@PathVariable UUID ticketId) {
        TicketDTO ticket = ticketService.getTicket(ticketId);
        return ResponseEntity.ok(ApiResponse.ok(ticket));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAuthority('PERM_TICKET_VIEW_ALL')")
    public ResponseEntity<ApiResponse<Page<TicketDTO>>> getByBranch(
            @PathVariable UUID branchId,
            @RequestParam(required = false) TicketStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TicketDTO> tickets = ticketService.getTicketsByBranch(branchId, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(tickets));
    }

    @PostMapping("/{ticketId}/status")
    @PreAuthorize("hasAuthority('PERM_TICKET_UPDATE_STATUS')")
    public ResponseEntity<ApiResponse<TicketDTO>> updateStatus(
            @PathVariable UUID ticketId,
            @Valid @RequestBody UpdateTicketStatusRequest request) {
        TicketDTO ticket = ticketService.updateStatus(ticketId, request);
        return ResponseEntity.ok(ApiResponse.ok(ticket, "Status updated"));
    }

    @PostMapping("/{ticketId}/diagnose")
    @PreAuthorize("hasAuthority('PERM_TICKET_ADD_DIAGNOSIS')")
    public ResponseEntity<ApiResponse<TicketDTO>> addDiagnosis(
            @PathVariable UUID ticketId,
            @Valid @RequestBody AddDiagnosisRequest request) {
        TicketDTO ticket = ticketService.addDiagnosis(ticketId, request);
        return ResponseEntity.ok(ApiResponse.ok(ticket, "Diagnosis step added"));
    }

    @PostMapping("/{ticketId}/assign/{techId}")
    @PreAuthorize("hasAuthority('PERM_TICKET_ASSIGN')")
    public ResponseEntity<ApiResponse<TicketDTO>> assignTech(
            @PathVariable UUID ticketId,
            @PathVariable UUID techId) {
        TicketDTO ticket = ticketService.assignTechnician(ticketId, techId);
        return ResponseEntity.ok(ApiResponse.ok(ticket, "Technician assigned"));
    }
}
