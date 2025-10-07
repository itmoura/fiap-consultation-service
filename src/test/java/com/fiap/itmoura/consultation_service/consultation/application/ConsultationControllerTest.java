package com.fiap.itmoura.consultation_service.consultation.application;

import com.fiap.itmoura.consultation_service.consultation.application.dto.request.ConsultationRequest;
import com.fiap.itmoura.consultation_service.consultation.application.dto.response.ConsultationResponse;
import com.fiap.itmoura.consultation_service.consultation.application.usecases.ConsultationService;
import com.fiap.itmoura.consultation_service.consultation.domain.enums.ConsultationStatusEnum;
import com.fiap.itmoura.consultation_service.user.application.domain.UserDTO;
import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultationControllerTest {

    @Mock
    private ConsultationService consultationService;

    @InjectMocks
    private ConsultationController consultationController;

    private ConsultationRequest consultationRequest;
    private ConsultationResponse consultationResponse;
    private UUID consultationId;

    @BeforeEach
    void setUp() {
        consultationId = UUID.randomUUID();
        
        consultationRequest = new ConsultationRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now().plusDays(1),
                LocalTime.of(1, 0),
                "Test consultation"
        );

        UserDTO medicDTO = new UserDTO(
                UUID.randomUUID(),
                "Dr. Test",
                "medic@example.com",
                "password123",
                TypeUserEnum.MEDIC,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        UserDTO patientDTO = new UserDTO(
                UUID.randomUUID(),
                "Patient Test",
                "patient@example.com",
                "password123",
                TypeUserEnum.PATIENT,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        consultationResponse = ConsultationResponse.builder()
                .id(consultationId)
                .medic(medicDTO)
                .patient(patientDTO)
                .startDate(LocalDateTime.now().plusDays(1))
                .finalDate(LocalDateTime.now().plusDays(1).plusHours(1))
                .status(ConsultationStatusEnum.SCHEDULED)
                .description("Test consultation")
                .build();
    }

    @Test
    void shouldFindAllConsultations() {
        when(consultationService.findAll()).thenReturn(List.of(consultationResponse));

        List<ConsultationResponse> result = consultationController.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(consultationResponse.getId(), result.get(0).getId());
        verify(consultationService).findAll();
    }

    @Test
    void shouldFindAllConsultationsByDate() {
        LocalDate date = LocalDate.now();
        when(consultationService.findAllByDate(date)).thenReturn(List.of(consultationResponse));

        List<ConsultationResponse> result = consultationController.findAllByDate(date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(consultationResponse.getId(), result.get(0).getId());
        verify(consultationService).findAllByDate(date);
    }

    @Test
    void shouldCreateConsultation() {
        when(consultationService.create(any(ConsultationRequest.class))).thenReturn(consultationResponse);

        ConsultationResponse result = consultationController.create(consultationRequest);

        assertNotNull(result);
        assertEquals(consultationResponse.getId(), result.getId());
        verify(consultationService).create(any(ConsultationRequest.class));
    }

    @Test
    void shouldUpdateConsultation() {
        when(consultationService.update(eq(consultationId), any(ConsultationRequest.class)))
                .thenReturn(consultationResponse);

        ConsultationResponse result = consultationController.update(consultationId, consultationRequest);

        assertNotNull(result);
        assertEquals(consultationResponse.getId(), result.getId());
        verify(consultationService).update(eq(consultationId), any(ConsultationRequest.class));
    }

    @Test
    void shouldConfirmConsultation() {
        when(consultationService.confirm(consultationId)).thenReturn(consultationResponse);

        ConsultationResponse result = consultationController.confirm(consultationId);

        assertNotNull(result);
        assertEquals(consultationResponse.getId(), result.getId());
        verify(consultationService).confirm(consultationId);
    }

    @Test
    void shouldCancelConsultation() {
        doNothing().when(consultationService).cancel(consultationId);

        consultationController.cancel(consultationId);

        verify(consultationService).cancel(consultationId);
    }
}
