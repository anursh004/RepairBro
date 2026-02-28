package com.repairbro.notification.repository;

import com.repairbro.notification.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    List<NotificationLog> findByTicketIdOrderByCreatedAtDesc(UUID ticketId);

    List<NotificationLog> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<NotificationLog> findByBranchIdOrderByCreatedAtDesc(UUID branchId);

    List<NotificationLog> findByStatusOrderByCreatedAtDesc(NotificationLog.Status status);

    List<NotificationLog> findTop50ByOrderByCreatedAtDesc();
}
