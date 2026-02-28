package com.repairbro.partsflow.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.partsflow.model.*;
import com.repairbro.partsflow.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/parts")
    @PreAuthorize("hasAuthority('PERM_PARTS_VIEW')")
    public ResponseEntity<ApiResponse<List<SparePart>>> getParts() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getAllParts()));
    }

    @PostMapping("/parts")
    @PreAuthorize("hasAuthority('PERM_PARTS_CREATE')")
    public ResponseEntity<ApiResponse<SparePart>> createPart(@RequestBody SparePart part) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(inventoryService.createPart(part)));
    }

    @GetMapping("/inventory/{branchId}")
    @PreAuthorize("hasAuthority('PERM_INVENTORY_VIEW')")
    public ResponseEntity<ApiResponse<List<BranchInventory>>> getInventory(@PathVariable UUID branchId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getBranchInventory(branchId)));
    }

    @GetMapping("/inventory/{branchId}/low-stock")
    @PreAuthorize("hasAuthority('PERM_INVENTORY_VIEW')")
    public ResponseEntity<ApiResponse<List<BranchInventory>>> getLowStock(@PathVariable UUID branchId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getLowStock(branchId)));
    }

    @PostMapping("/inventory/reserve")
    @PreAuthorize("hasAuthority('PERM_INVENTORY_RESERVE')")
    public ResponseEntity<ApiResponse<BranchInventory>> reserve(@RequestBody Map<String, String> req) {
        UUID branchId = UUID.fromString(req.get("branchId"));
        UUID partId = UUID.fromString(req.get("sparePartId"));
        int qty = Integer.parseInt(req.get("quantity"));
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.reserveParts(branchId, partId, qty)));
    }

    @PostMapping("/procurement/order")
    @PreAuthorize("hasAuthority('PERM_PROCUREMENT_ORDER')")
    public ResponseEntity<ApiResponse<ProcurementOrder>> placeOrder(@RequestBody ProcurementOrder order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(inventoryService.placeOrder(order)));
    }
}
