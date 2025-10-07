package com.fiap.itmoura.consultation_service.consultation.infrastructure.persistence;

import com.fiap.itmoura.consultation_service.consultation.domain.Consultation;
import com.fiap.itmoura.consultation_service.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, UUID> {

    @Query("""
        SELECT c FROM Consultation c
        WHERE c.medic.id = :medicId
          AND c.startDate < :finalDate
          AND c.finalDate > :startDate
          AND (c.status IN ('SCHEDULED', 'CONFIRMED'))
    """)
    Optional<Consultation> findConflictingConsultation(UUID medicId, LocalDateTime startDate, LocalDateTime finalDate);

    @Query("""
        SELECT c FROM Consultation c
        WHERE c.startDate BETWEEN :startDate AND :finalDate
    """)
    List<Consultation> findAllByDate(LocalDateTime startDate, LocalDateTime finalDate);
}