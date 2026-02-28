package com.repairbro.diagflow.repository;

import com.repairbro.diagflow.model.DiagnosisEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiagnosisEvaluationRepository extends JpaRepository<DiagnosisEvaluation, UUID> {
    List<DiagnosisEvaluation> findByTicketId(UUID ticketId);
}
