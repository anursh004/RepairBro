package com.repairbro.repaircore.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 128)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 64)
    private String city;

    @Column(length = 10)
    private String pincode;

    /** Preferred communication channel: SMS, EMAIL, WHATSAPP */
    @Column(length = 20)
    @Builder.Default
    private String preferredChannel = "SMS";

    /** Total number of repairs done (denormalized for quick lookup) */
    @Builder.Default
    private int totalRepairs = 0;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
