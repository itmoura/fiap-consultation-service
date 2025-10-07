package com.fiap.itmoura.consultation_service.consultation.application.usecases.impl;

import com.fiap.itmoura.consultation_service.consultation.application.dto.request.ConsultationRequest;
import com.fiap.itmoura.consultation_service.consultation.application.dto.response.ConsultationResponse;
import com.fiap.itmoura.consultation_service.consultation.domain.Consultation;
import com.fiap.itmoura.consultation_service.consultation.domain.enums.ConsultationStatusEnum;
import com.fiap.itmoura.consultation_service.consultation.infrastructure.persistence.ConsultationRepository;
import com.fiap.itmoura.consultation_service.shared.exception.BadRequestException;
import com.fiap.itmoura.consultation_service.shared.exception.ConflictRequestException;
import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import com.fiap.itmoura.consultation_service.user.application.service.UserService;
import com.fiap.itmoura.consultation_service.user.domain.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceImplTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ConsultationServiceImpl consultationService;

    private Users medic;
    private Users patient;
    private Consultation consultation;
    private ConsultationRequest consultationRequest;
    private UUID consultationId;

    @BeforeEach
    void setUp() {
        consultationId = UUID.randomUUID();
        
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

        consultation = Consultation.create(
                medic,
                patient,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                "Test consultation"
        );
        consultation.setId(consultationId);

        consultationRequest = new ConsultationRequest(
                medic.getId(),
                patient.getId(),
                LocalDateTime.now().plusDays(1),
                LocalTime.of(1, 0),
                "Test consultation"
        );
    }

    @Test
    void shouldFindAllConsultations() {
        when(consultationRepository.findAll()).thenReturn(List.of(consultation));

        List<ConsultationResponse> result = consultationService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(consultationRepository).findAll();
    }

    @Test
    void shouldFindAllByDate() {
        LocalDate date = LocalDate.now();
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime finalDate = date.atTime(LocalTime.MAX);
        
        when(consultationRepository.findAllByDate(startDate, finalDate)).thenReturn(List.of(consultation));

        List<ConsultationResponse> result = consultationService.findAllByDate(date);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(consultationRepository).findAllByDate(startDate, finalDate);
    }

    @Test
    void shouldCreateConsultation() {
        when(userService.findByIdUsers(medic.getId())).thenReturn(medic);
        when(userService.findByIdUsers(patient.getId())).thenReturn(patient);
        when(consultationRepository.findConflictingConsultation(any(), any(), any())).thenReturn(Optional.empty());
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        ConsultationResponse result = consultationService.create(consultationRequest);

        assertNotNull(result);
        verify(userService).findByIdUsers(medic.getId());
        verify(userService).findByIdUsers(patient.getId());
        verify(consultationRepository).save(any(Consultation.class));
    }

    @Test
    void shouldThrowExceptionWhenStartDateIsInPast() {
        ConsultationRequest pastRequest = new ConsultationRequest(
                medic.getId(),
                patient.getId(),
                LocalDateTime.now().minusDays(1),
                LocalTime.of(1, 0),
                "Test consultation"
        );

        assertThrows(BadRequestException.class, () -> consultationService.create(pastRequest));
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotMedic() {
        Users nonMedic = Users.builder()
                .id(UUID.randomUUID())
                .name("Non Medic")
                .email("nonmedic@example.com")
                .typeUserRole(TypeUserEnum.PATIENT)
                .isActive(true)
                .build();

        when(userService.findByIdUsers(medic.getId())).thenReturn(nonMedic);
        when(userService.findByIdUsers(patient.getId())).thenReturn(patient);

        assertThrows(BadRequestException.class, () -> consultationService.create(consultationRequest));
    }

    @Test
    void shouldThrowExceptionWhenMedicAndPatientAreSame() {
        ConsultationRequest sameUserRequest = new ConsultationRequest(
                medic.getId(),
                medic.getId(),
                LocalDateTime.now().plusDays(1),
                LocalTime.of(1, 0),
                "Test consultation"
        );

        when(userService.findByIdUsers(medic.getId())).thenReturn(medic);

        assertThrows(BadRequestException.class, () -> consultationService.create(sameUserRequest));
    }

    @Test
    void shouldThrowExceptionWhenMedicHasConflictingConsultation() {
        when(userService.findByIdUsers(medic.getId())).thenReturn(medic);
        when(userService.findByIdUsers(patient.getId())).thenReturn(patient);
        when(consultationRepository.findConflictingConsultation(any(), any(), any()))
                .thenReturn(Optional.of(consultation));

        assertThrows(ConflictRequestException.class, () -> consultationService.create(consultationRequest));
    }

    @Test
    void shouldConfirmConsultation() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(patient.getEmail());
        when(userService.findByEmailUsers(patient.getEmail())).thenReturn(patient);
        when(consultationRepository.findById(consultationId)).thenReturn(Optional.of(consultation));
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        ConsultationResponse result = consultationService.confirm(consultationId);

        assertNotNull(result);
        verify(consultationRepository).save(any(Consultation.class));
    }

    @Test
    void shouldThrowExceptionWhenNonPatientTriesToConfirm() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(medic.getEmail());
        when(userService.findByEmailUsers(medic.getEmail())).thenReturn(medic);
        when(consultationRepository.findById(consultationId)).thenReturn(Optional.of(consultation));

        assertThrows(BadRequestException.class, () -> consultationService.confirm(consultationId));
    }

    @Test
    void shouldCancelConsultation() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(patient.getEmail());
        when(userService.findByEmailUsers(patient.getEmail())).thenReturn(patient);
        when(consultationRepository.findById(consultationId)).thenReturn(Optional.of(consultation));
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        consultationService.cancel(consultationId);

        verify(consultationRepository).save(any(Consultation.class));
    }

    @Test
    void shouldThrowExceptionWhenConsultationNotFound() {
        when(consultationRepository.findById(consultationId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> consultationService.confirm(consultationId));
    }

    @Test
    void shouldThrowExceptionWhenTryingToConfirmCancelledConsultation() {
        consultation.setStatus(ConsultationStatusEnum.CANCELLED);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(patient.getEmail());
        when(userService.findByEmailUsers(patient.getEmail())).thenReturn(patient);
        when(consultationRepository.findById(consultationId)).thenReturn(Optional.of(consultation));

        assertThrows(BadRequestException.class, () -> consultationService.confirm(consultationId));
    }
}
