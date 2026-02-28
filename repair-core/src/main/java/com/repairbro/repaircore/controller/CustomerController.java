package com.repairbro.repaircore.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.repaircore.dto.CreateCustomerRequest;
import com.repairbro.repaircore.dto.CustomerDTO;
import com.repairbro.repaircore.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_CUSTOMER_CREATE')")
    public ResponseEntity<ApiResponse<CustomerDTO>> create(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerDTO dto = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(dto, "Customer created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_CUSTOMER_VIEW')")
    public ResponseEntity<ApiResponse<CustomerDTO>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getCustomer(id)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_CUSTOMER_SEARCH')")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getAll()));
    }

    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasAuthority('PERM_CUSTOMER_SEARCH')")
    public ResponseEntity<ApiResponse<CustomerDTO>> getByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getByPhone(phone)));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('PERM_CUSTOMER_SEARCH')")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> search(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.searchByName(name)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_CUSTOMER_EDIT')")
    public ResponseEntity<ApiResponse<CustomerDTO>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.updateCustomer(id, request), "Customer updated"));
    }
}
