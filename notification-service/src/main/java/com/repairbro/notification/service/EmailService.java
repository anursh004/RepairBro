package com.repairbro.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Real email sending service using Spring JavaMail (SMTP).
 * Supports HTML email bodies for professional-looking notifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${repairbro.notification.from-email:noreply@repairbro.in}")
    private String fromEmail;

    @Value("${repairbro.notification.from-name:RepairBro}")
    private String fromName;

    @Value("${repairbro.notification.email-enabled:true}")
    private boolean emailEnabled;

    /**
     * Send an HTML email. Returns true if sent successfully.
     */
    public boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        if (!emailEnabled) {
            log.info("[EMAIL-DISABLED] Would send to: {} | Subject: {}", to, subject);
            return true;
        }

        if (to == null || to.isBlank()) {
            log.warn("[EMAIL] Cannot send — recipient is blank. Subject: {}", subject);
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML

            mailSender.send(message);
            log.info("[EMAIL-SENT] To: {} | Subject: {}", to, subject);
            return true;
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("[EMAIL-FAILED] To: {} | Subject: {} | Error: {}", to, subject, e.getMessage());
            return false;
        }
    }

    /**
     * Send a plain-text email.
     */
    public boolean sendPlainEmail(String to, String subject, String textBody) {
        if (!emailEnabled) {
            log.info("[EMAIL-DISABLED] Would send to: {} | Subject: {}", to, subject);
            return true;
        }

        if (to == null || to.isBlank()) {
            log.warn("[EMAIL] Cannot send — recipient is blank. Subject: {}", subject);
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(textBody, false); // false = plain text

            mailSender.send(message);
            log.info("[EMAIL-SENT] To: {} | Subject: {}", to, subject);
            return true;
        } catch (MessagingException e) {
            log.error("[EMAIL-FAILED] To: {} | Subject: {} | Error: {}", to, subject, e.getMessage());
            return false;
        }
    }
}
