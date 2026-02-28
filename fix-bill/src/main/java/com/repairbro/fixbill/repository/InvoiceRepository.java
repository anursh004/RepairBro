package com.repairbro.fixbill.repository;

import com.repairbro.fixbill.model.Invoice;
import com.repairbro.fixbill.model.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByTicketId(UUID ticketId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Page<Invoice> findByBranchId(UUID branchId, Pageable pageable);

    Page<Invoice> findByBranchIdAndStatus(UUID branchId, InvoiceStatus status, Pageable pageable);

    Page<Invoice> findByCustomerId(UUID customerId, Pageable pageable);
}
