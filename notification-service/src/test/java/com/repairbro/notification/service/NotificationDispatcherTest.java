package com.repairbro.notification.service;

import com.repairbro.notification.model.NotificationLog;
import com.repairbro.notification.repository.NotificationLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationDispatcher Unit Tests")
class NotificationDispatcherTest {

    @Mock
    private EmailService emailService;
    @Mock
    private NotificationLogRepository logRepo;
    @InjectMocks
    private NotificationDispatcher dispatcher;

    @Nested
    @DisplayName("sendEmail()")
    class SendEmail {

        @Test
        @DisplayName("should log SENT status on successful email")
        void shouldLogSent() {
            when(logRepo.save(any(NotificationLog.class))).thenAnswer(inv -> inv.getArgument(0));
            when(emailService.sendHtmlEmail(anyString(), anyString(), anyString())).thenReturn(true);

            dispatcher.sendEmail("test@repairbro.in", "Test Subject", "<p>Hello</p>");

            ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
            verify(logRepo).save(captor.capture());

            NotificationLog log = captor.getValue();
            assertThat(log.getChannel()).isEqualTo(NotificationLog.Channel.EMAIL);
            assertThat(log.getRecipient()).isEqualTo("test@repairbro.in");
            assertThat(log.getSubject()).isEqualTo("Test Subject");
            assertThat(log.getStatus()).isEqualTo(NotificationLog.Status.SENT);
            assertThat(log.getErrorMessage()).isNull();
        }

        @Test
        @DisplayName("should log FAILED status on email failure")
        void shouldLogFailed() {
            when(logRepo.save(any(NotificationLog.class))).thenAnswer(inv -> inv.getArgument(0));
            when(emailService.sendHtmlEmail(anyString(), anyString(), anyString())).thenReturn(false);

            dispatcher.sendEmail("bad@email.com", "Test", "<p>Hi</p>");

            ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
            verify(logRepo).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(NotificationLog.Status.FAILED);
        }
    }

    @Nested
    @DisplayName("notifyTicketCreated()")
    class TicketCreated {

        @Test
        @DisplayName("should send both email and SMS for ticket creation")
        void shouldSendEmailAndSms() {
            when(logRepo.save(any(NotificationLog.class))).thenAnswer(inv -> inv.getArgument(0));
            when(emailService.sendHtmlEmail(anyString(), anyString(), anyString())).thenReturn(true);

            Map<String, String> data = Map.of(
                    "customerEmail", "customer@test.com",
                    "customerPhone", "9876543210",
                    "customerName", "Rajesh",
                    "ticketId", "TKT-001",
                    "deviceModel", "iPhone 15",
                    "branchName", "Mumbai Central");

            dispatcher.notifyTicketCreated(data);

            // Should save 2 logs: 1 email + 1 SMS
            verify(logRepo, times(2)).save(any(NotificationLog.class));
        }
    }

    @Nested
    @DisplayName("notifyEstimateReady()")
    class EstimateReady {

        @Test
        @DisplayName("should include cost in email subject")
        void shouldIncludeCost() {
            when(logRepo.save(any(NotificationLog.class))).thenAnswer(inv -> inv.getArgument(0));
            when(emailService.sendHtmlEmail(anyString(), contains("₹3500"), anyString())).thenReturn(true);

            Map<String, String> data = Map.of(
                    "customerEmail", "customer@test.com",
                    "totalEstimate", "3500");

            dispatcher.notifyEstimateReady(data);

            verify(emailService).sendHtmlEmail(eq("customer@test.com"), contains("₹3500"), anyString());
        }
    }

    @Nested
    @DisplayName("notifySLABreach()")
    class SLABreach {

        @Test
        @DisplayName("should send to manager email with SLA_BREACH template")
        void shouldSendToManager() {
            when(logRepo.save(any(NotificationLog.class))).thenAnswer(inv -> inv.getArgument(0));
            when(emailService.sendHtmlEmail(anyString(), anyString(), anyString())).thenReturn(true);

            Map<String, String> data = Map.of(
                    "managerEmail", "manager@repairbro.in",
                    "ticketId", "TKT-999",
                    "slaType", "URGENT_24H",
                    "branchName", "Mumbai Central");

            dispatcher.notifySLABreach(data);

            ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
            verify(logRepo).save(captor.capture());
            assertThat(captor.getValue().getTemplateKey()).isEqualTo("SLA_BREACH");
        }
    }

    @Nested
    @DisplayName("Template generation")
    class Templates {

        @Test
        @DisplayName("ticketCreated template should contain customer name")
        void ticketCreatedTemplate() {
            String html = NotificationTemplates.ticketCreated(Map.of("customerName", "Priya Patel"));
            assertThat(html).contains("Priya Patel");
            assertThat(html).contains("Repair Ticket Created");
        }

        @Test
        @DisplayName("slaBreachAlert template should contain ticket ID")
        void slaBreachTemplate() {
            String html = NotificationTemplates.slaBreachAlert(Map.of("ticketId", "TKT-123"));
            assertThat(html).contains("TKT-123");
            assertThat(html).contains("SLA Breach");
        }

        @Test
        @DisplayName("paymentConfirmation template should contain amount and method")
        void paymentTemplate() {
            String html = NotificationTemplates.paymentConfirmation(Map.of(
                    "amount", "5200", "method", "UPI", "invoiceNumber", "INV-001"));
            assertThat(html).contains("5200");
            assertThat(html).contains("UPI");
        }
    }
}
