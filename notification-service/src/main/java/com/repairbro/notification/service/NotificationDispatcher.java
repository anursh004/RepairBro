package com.repairbro.notification.service;

import com.repairbro.notification.model.NotificationLog;
import com.repairbro.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Central notification dispatcher — routes to Email, SMS, or Push,
 * logs every notification to the database, and handles failures gracefully.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final EmailService emailService;
    private final NotificationLogRepository logRepo;

    @Value("${repairbro.notification.sms-enabled:false}")
    private boolean smsEnabled;

    // ──────────────────────────────────────────────────────────
    // PUBLIC DISPATCH METHODS
    // ──────────────────────────────────────────────────────────

    /**
     * Send an HTML email and log the result.
     */
    public void sendEmail(String to, String subject, String htmlBody) {
        sendEmail(to, subject, htmlBody, null, null, null, null, null);
    }

    /**
     * Send an HTML email with full context for logging.
     */
    public void sendEmail(String to, String subject, String htmlBody,
            String templateKey, String eventType,
            UUID ticketId, UUID customerId, UUID branchId) {
        boolean success = emailService.sendHtmlEmail(to, subject, htmlBody);

        NotificationLog entry = NotificationLog.builder()
                .channel(NotificationLog.Channel.EMAIL)
                .recipient(to != null ? to : "unknown")
                .subject(subject)
                .body(htmlBody)
                .templateKey(templateKey)
                .eventType(eventType)
                .ticketId(ticketId)
                .customerId(customerId)
                .branchId(branchId)
                .status(success ? NotificationLog.Status.SENT : NotificationLog.Status.FAILED)
                .errorMessage(success ? null : "SMTP delivery failed")
                .build();

        logRepo.save(entry);
    }

    /**
     * Send an SMS notification. Logs to DB.
     * When SMS is disabled, messages are logged as SENT (dry run).
     */
    public void sendSms(String phone, String message) {
        sendSms(phone, message, null, null, null, null);
    }

    public void sendSms(String phone, String message,
            String eventType, UUID ticketId, UUID customerId, UUID branchId) {
        boolean success;

        if (smsEnabled && phone != null && !phone.isBlank()) {
            // In production, integrate MSG91 / Twilio here
            // For now, SMS is logged but not sent externally
            log.info("[SMS-SEND] To: {} | Message: {}", phone, message);
            success = true;
        } else {
            log.info("[SMS-DRY-RUN] To: {} | Message: {}", phone, message);
            success = true; // Dry run is always "successful"
        }

        NotificationLog entry = NotificationLog.builder()
                .channel(NotificationLog.Channel.SMS)
                .recipient(phone != null ? phone : "unknown")
                .subject(null)
                .body(message)
                .templateKey(null)
                .eventType(eventType)
                .ticketId(ticketId)
                .customerId(customerId)
                .branchId(branchId)
                .status(success ? NotificationLog.Status.SENT : NotificationLog.Status.FAILED)
                .build();

        logRepo.save(entry);
    }

    /**
     * Send push notification (FCM). Currently logs only.
     */
    public void sendPush(String userId, String title, String body) {
        log.info("[PUSH] To: {} | Title: {} | Body: {}", userId, title, body);

        NotificationLog entry = NotificationLog.builder()
                .channel(NotificationLog.Channel.PUSH)
                .recipient(userId != null ? userId : "unknown")
                .subject(title)
                .body(body)
                .status(NotificationLog.Status.SENT)
                .build();

        logRepo.save(entry);
    }

    // ──────────────────────────────────────────────────────────
    // CONVENIENCE TEMPLATE METHODS
    // ──────────────────────────────────────────────────────────

    public void notifyTicketCreated(Map<String, String> data) {
        String email = data.getOrDefault("customerEmail", "");
        String phone = data.getOrDefault("customerPhone", "");
        String html = NotificationTemplates.ticketCreated(data);
        UUID ticketId = parseUuid(data.get("ticketId"));
        UUID customerId = parseUuid(data.get("customerId"));
        UUID branchId = parseUuid(data.get("branchId"));

        sendEmail(email, "🎫 Repair Ticket Created — " + data.getOrDefault("ticketId", ""),
                html, "TICKET_CREATED", "TicketCreated", ticketId, customerId, branchId);

        sendSms(phone, "RepairBro: Your repair ticket " + data.getOrDefault("ticketId", "")
                + " has been created for " + data.getOrDefault("deviceModel", "your device")
                + ". We'll keep you updated!",
                "TicketCreated", ticketId, customerId, branchId);
    }

    public void notifyTicketStatusUpdate(Map<String, String> data) {
        String email = data.getOrDefault("customerEmail", "");
        String phone = data.getOrDefault("customerPhone", "");
        String html = NotificationTemplates.ticketStatusUpdate(data);
        UUID ticketId = parseUuid(data.get("ticketId"));

        sendEmail(email, "📋 Ticket Update — " + data.getOrDefault("newStatus", ""),
                html, "TICKET_STATUS_UPDATE", "TicketStatusChanged", ticketId, null, null);

        sendSms(phone, "RepairBro: Your ticket " + data.getOrDefault("ticketId", "")
                + " status updated to " + data.getOrDefault("newStatus", ""),
                "TicketStatusChanged", ticketId, null, null);
    }

    public void notifyReadyForPickup(Map<String, String> data) {
        String email = data.getOrDefault("customerEmail", "");
        String phone = data.getOrDefault("customerPhone", "");
        String html = NotificationTemplates.ticketReadyForPickup(data);
        UUID ticketId = parseUuid(data.get("ticketId"));

        sendEmail(email, "🎉 Your Device is Ready for Pickup!",
                html, "READY_FOR_PICKUP", "TicketReadyForPickup", ticketId, null, null);

        sendSms(phone, "RepairBro: Great news! Your device is ready for pickup at "
                + data.getOrDefault("branchName", "our branch") + ". Amount due: ₹"
                + data.getOrDefault("finalCost", "0"),
                "TicketReadyForPickup", ticketId, null, null);
    }

    public void notifyEstimateReady(Map<String, String> data) {
        String email = data.getOrDefault("customerEmail", "");
        String html = NotificationTemplates.estimateReady(data);
        UUID ticketId = parseUuid(data.get("ticketId"));

        sendEmail(email, "💰 Repair Estimate Ready — ₹" + data.getOrDefault("totalEstimate", ""),
                html, "ESTIMATE_READY", "EstimateCreated", ticketId, null, null);
    }

    public void notifyPaymentReceived(Map<String, String> data) {
        String email = data.getOrDefault("customerEmail", "");
        String html = NotificationTemplates.paymentConfirmation(data);

        sendEmail(email, "✅ Payment Confirmation — ₹" + data.getOrDefault("amount", ""),
                html, "PAYMENT_CONFIRMATION", "PaymentReceived", null, null, null);
    }

    public void notifySLABreach(Map<String, String> data) {
        String managerEmail = data.getOrDefault("managerEmail", "");
        String html = NotificationTemplates.slaBreachAlert(data);
        UUID ticketId = parseUuid(data.get("ticketId"));
        UUID branchId = parseUuid(data.get("branchId"));

        sendEmail(managerEmail, "⚠️ SLA Breach Alert — Ticket " + data.getOrDefault("ticketId", ""),
                html, "SLA_BREACH", "SLABreached", ticketId, null, branchId);
    }

    public void notifyWelcome(Map<String, String> data) {
        String email = data.getOrDefault("email", "");
        String html = NotificationTemplates.welcomeUser(data);

        sendEmail(email, "👋 Welcome to RepairBro!",
                html, "WELCOME", "UserCreated", null, null, null);
    }

    public void notifyWarrantyIssued(Map<String, String> data) {
        String email = data.getOrDefault("customerEmail", "");
        String html = NotificationTemplates.warrantyIssued(data);
        UUID ticketId = parseUuid(data.get("ticketId"));

        sendEmail(email, "🛡️ Warranty Certificate — " + data.getOrDefault("warrantyDays", "30") + " Days",
                html, "WARRANTY_ISSUED", "WarrantyIssued", ticketId, null, null);
    }

    // ──────────────────────────────────────────────────────────
    // QUERY METHODS
    // ──────────────────────────────────────────────────────────

    public List<NotificationLog> getRecentNotifications() {
        return logRepo.findTop50ByOrderByCreatedAtDesc();
    }

    public List<NotificationLog> getByTicket(UUID ticketId) {
        return logRepo.findByTicketIdOrderByCreatedAtDesc(ticketId);
    }

    public List<NotificationLog> getByCustomer(UUID customerId) {
        return logRepo.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<NotificationLog> getFailedNotifications() {
        return logRepo.findByStatusOrderByCreatedAtDesc(NotificationLog.Status.FAILED);
    }

    private UUID parseUuid(String s) {
        try {
            return s != null ? UUID.fromString(s) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
