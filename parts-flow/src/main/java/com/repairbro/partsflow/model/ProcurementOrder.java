package com.repairbro.partsflow.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "procurement_order")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProcurementOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String orderNumber;

    @Column(nullable = false)
    private UUID branchId;

    @Column(nullable = false)
    private UUID sparePartId;

    @Column(nullable = false)
    private int quantity;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalCost;

    @Column(length = 128)
    private String supplierName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @CreationTimestamp
    private Instant createdAt;

    private Instant expectedDelivery;
    private Instant deliveredAt;

    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
}
