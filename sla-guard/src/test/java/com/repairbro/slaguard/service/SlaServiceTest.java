package com.repairbro.slaguard.service;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.slaguard.model.*;
import com.repairbro.slaguard.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SlaService Unit Tests")
class SlaServiceTest {

    @Mock
    private SlaRecordRepository slaRepo;
    @Mock
    private ComplaintRepository complaintRepo;
    @Mock
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;
    @InjectMocks
    private SlaService slaService;

    private UUID ticketId, branchId;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();
        branchId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("createSla()")
    class CreateSla {

        @Test
        @DisplayName("should create SLA record with correct data")
        void shouldCreate() {
            Instant deadline = Instant.now().plus(48, ChronoUnit.HOURS);
            SlaRecord expected = SlaRecord.builder()
                    .ticketId(ticketId).branchId(branchId)
                    .slaType("STANDARD_48H").deadline(deadline).build();

            when(slaRepo.save(any(SlaRecord.class))).thenReturn(expected);

            SlaRecord result = slaService.createSla(ticketId, branchId, "STANDARD_48H", deadline);

            assertThat(result.getTicketId()).isEqualTo(ticketId);
            assertThat(result.getSlaType()).isEqualTo("STANDARD_48H");
            verify(slaRepo).save(any(SlaRecord.class));
        }
    }

    @Nested
    @DisplayName("checkBreaches()")
    class CheckBreaches {

        @Test
        @DisplayName("should mark active SLAs past deadline as BREACHED")
        void shouldDetectBreaches() {
            SlaRecord breached = SlaRecord.builder()
                    .ticketId(ticketId).branchId(branchId)
                    .slaType("URGENT_24H")
                    .deadline(Instant.now().minus(1, ChronoUnit.HOURS))
                    .status(SlaRecord.SlaStatus.ACTIVE)
                    .build();

            when(slaRepo.findBreachedSlas(any(Instant.class))).thenReturn(List.of(breached));
            when(slaRepo.save(any(SlaRecord.class))).thenAnswer(inv -> inv.getArgument(0));

            slaService.checkBreaches();

            assertThat(breached.getStatus()).isEqualTo(SlaRecord.SlaStatus.BREACHED);
            verify(slaRepo).save(breached);
            verify(kafkaTemplate).send(anyString(), anyString(), any(DomainEvent.class));
        }

        @Test
        @DisplayName("should do nothing when no breaches found")
        void shouldNotBreachWhenNone() {
            when(slaRepo.findBreachedSlas(any(Instant.class))).thenReturn(List.of());

            slaService.checkBreaches();

            verify(slaRepo, never()).save(any());
        }
    }

    @Nested
    @DisplayName("fileComplaint()")
    class FileComplaint {

        @Test
        @DisplayName("should save complaint and publish event")
        void shouldFileComplaint() {
            Complaint complaint = Complaint.builder()
                    .id(UUID.randomUUID())
                    .ticketId(ticketId).branchId(branchId)
                    .description("Repair took too long")
                    .build();

            when(complaintRepo.save(any(Complaint.class))).thenReturn(complaint);

            Complaint result = slaService.fileComplaint(complaint);

            assertThat(result.getDescription()).isEqualTo("Repair took too long");
            verify(complaintRepo).save(any());
            verify(kafkaTemplate).send(anyString(), anyString(), any(DomainEvent.class));
        }
    }

    @Nested
    @DisplayName("getComplaints()")
    class GetComplaints {

        @Test
        @DisplayName("should return complaints by branch")
        void shouldReturnByBranch() {
            Complaint c1 = Complaint.builder().branchId(branchId).description("Issue 1").build();
            Complaint c2 = Complaint.builder().branchId(branchId).description("Issue 2").build();

            when(complaintRepo.findByBranchId(branchId)).thenReturn(List.of(c1, c2));

            List<Complaint> results = slaService.getComplaints(branchId);
            assertThat(results).hasSize(2);
        }
    }
}
