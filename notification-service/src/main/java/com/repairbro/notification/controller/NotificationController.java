package com.repairbro.notification.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.notification.model.NotificationLog;
import com.repairbro.notification.service.NotificationDispatcher;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationDispatcher dispatcher;

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_NOTIFICATION_VIEW')")
    public ResponseEntity<ApiResponse<List<NotificationLog>>> getRecent() {
        return ResponseEntity.ok(ApiResponse.ok(dispatcher.getRecentNotifications()));
    }

    @GetMapping("/ticket/{ticketId}")
    @PreAuthorize("hasAuthority('PERM_NOTIFICATION_VIEW')")
    public ResponseEntity<ApiResponse<List<NotificationLog>>> getByTicket(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(ApiResponse.ok(dispatcher.getByTicket(ticketId)));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('PERM_NOTIFICATION_VIEW')")
    public ResponseEntity<ApiResponse<List<NotificationLog>>> getByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.ok(dispatcher.getByCustomer(customerId)));
    }

    @GetMapping("/failed")
    @PreAuthorize("hasAuthority('PERM_NOTIFICATION_VIEW')")
    public ResponseEntity<ApiResponse<List<NotificationLog>>> getFailed() {
        return ResponseEntity.ok(ApiResponse.ok(dispatcher.getFailedNotifications()));
    }

    @PostMapping("/test-email")
    @PreAuthorize("hasAuthority('PERM_NOTIFICATION_SEND')")
    public ResponseEntity<ApiResponse<String>> testEmail(@RequestBody TestEmailRequest req) {
        dispatcher.sendEmail(req.getTo(), "RepairBro Test Email",
                "<h2>SMTP Configuration Test</h2><p>If you're reading this, your email setup is working! 🎉</p>",
                "TEST", "TestEmail", null, null, null);
        return ResponseEntity.ok(ApiResponse.ok("Test email sent to " + req.getTo()));
    }

    @PostMapping("/send")
    @PreAuthorize("hasAuthority('PERM_NOTIFICATION_SEND')")
    public ResponseEntity<ApiResponse<String>> sendManual(@RequestBody ManualNotificationRequest req) {
        if ("EMAIL".equalsIgnoreCase(req.getChannel())) {
            dispatcher.sendEmail(req.getRecipient(), req.getSubject(), req.getBody(),
                    "MANUAL", "ManualNotification", null, null, null);
        } else if ("SMS".equalsIgnoreCase(req.getChannel())) {
            dispatcher.sendSms(req.getRecipient(), req.getBody(),
                    "ManualNotification", null, null, null);
        }
        return ResponseEntity.ok(ApiResponse.ok("Notification sent via " + req.getChannel()));
    }

    @Data
    public static class TestEmailRequest {
        private String to;
    }

    @Data
    public static class ManualNotificationRequest {
        private String channel; // EMAIL or SMS
        private String recipient;
        private String subject;
        private String body;
    }
}
