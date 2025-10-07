package com.fiap.itmoura.consultation_service.consultation.domain;

import com.fiap.itmoura.consultation_service.consultation.domain.enums.ConsultationStatusEnum;
import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import com.fiap.itmoura.consultation_service.user.domain.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConsultationTest {

    private Users medic;
    private Users patient;
    private LocalDateTime startDate;
    private LocalDateTime finalDate;
    private String description;

    @BeforeEach
    void setUp() {
        medic = Users.builder()
                .id(UUID.randomUUID())
                .name("Dr. Test")
                .email("medic@example.com")
                .password("password123")
                .typeUserRole(TypeUserEnum.MEDIC)
                .isActive(true)
                .build();

        patient = Users.builder()
                .id(UUID.randomUUID())
                .name("Patient Test")
                .email("patient@example.com")
                .password("password123")
                .typeUserRole(TypeUserEnum.PATIENT)
                .isActive(true)
                .build();

        startDate = LocalDateTime.now().plusDays(1);
        finalDate = startDate.plusHours(1);
        description = "Test consultation";
    }

    @Test
    void shouldCreateConsultationWithFactoryMethod() {
        Consultation consultation = Consultation.create(medic, patient, startDate, finalDate, description);

        assertNotNull(consultation);
        assertEquals(medic, consultation.getMedic());
        assertEquals(patient, consultation.getPatient());
        assertEquals(startDate, consultation.getStartDate());
        assertEquals(finalDate, consultation.getFinalDate());
        assertEquals(description, consultation.getDescription());
        assertEquals(ConsultationStatusEnum.SCHEDULED, consultation.getStatus());
    }

    @Test
    void shouldUpdateTimestampOnUpdate() {
        Consultation consultation = new Consultation();
        consultation.onCreate();
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        consultation.onUpdate();
        
        assertNotNull(consultation.getUpdatedAt());
    }

    @Test
    void shouldSetAndGetAllProperties() {
        Consultation consultation = new Consultation();
        UUID id = UUID.randomUUID();
        
        consultation.setId(id);
        consultation.setMedic(medic);
        consultation.setPatient(patient);
        consultation.setStartDate(startDate);
        consultation.setFinalDate(finalDate);
        consultation.setDescription(description);
        consultation.setStatus(ConsultationStatusEnum.COMPLETED);
        
        assertEquals(id, consultation.getId());
        assertEquals(medic, consultation.getMedic());
        assertEquals(patient, consultation.getPatient());
        assertEquals(startDate, consultation.getStartDate());
        assertEquals(finalDate, consultation.getFinalDate());
        assertEquals(description, consultation.getDescription());
        assertEquals(ConsultationStatusEnum.COMPLETED, consultation.getStatus());
    }
}
