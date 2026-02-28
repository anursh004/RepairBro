package com.repairbro.notification.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Channel channel;

    @Column(nullable = false, length = 256)
    private String recipient;

    @Column(length = 256)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(length = 64)
    private String templateKey;

    @Column(length = 128)
    private String eventType;

    private UUID ticketId;
    private UUID customerId;
    private UUID branchId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.SENT;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Builder.Default
    private int retryCount = 0;

    @CreationTimestamp
    private Instant createdAt;

    public enum Channel {
        EMAIL, SMS, PUSH
    }

    public enum Status {
        SENT, FAILED, QUEUED
    }
}
