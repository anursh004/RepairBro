package com.repairbro.partsflow.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "supplier")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 128)
    private String contactPerson;

    @Column(length = 128)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    /** Standard delivery SLA in hours (e.g. 48 = 2 days) */
    @Builder.Default
    private int deliverySlaHours = 72;

    /** Rating 1.0 to 5.0 */
    @Builder.Default
    private double rating = 3.0;

    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    private Instant createdAt;
}
