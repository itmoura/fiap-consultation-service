package com.fiap.itmoura.consultation_service.consultation.application.usecases.impl;

import com.fiap.itmoura.consultation_service.consultation.application.dto.request.ConsultationRequest;
import com.fiap.itmoura.consultation_service.consultation.application.dto.response.ConsultationResponse;
import com.fiap.itmoura.consultation_service.consultation.application.usecases.ConsultationService;
import com.fiap.itmoura.consultation_service.consultation.domain.Consultation;
import com.fiap.itmoura.consultation_service.consultation.domain.enums.ConsultationStatusEnum;
import com.fiap.itmoura.consultation_service.consultation.infrastructure.persistence.ConsultationRepository;
import com.fiap.itmoura.consultation_service.shared.exception.BadRequestException;
import com.fiap.itmoura.consultation_service.shared.exception.ConflictRequestException;
import com.fiap.itmoura.consultation_service.user.application.service.UserService;
import com.fiap.itmoura.consultation_service.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final UserService userService;

    @Override
    public List<ConsultationResponse> findAll() {
        return consultationRepository
                .findAll()
                .stream()
                .sorted((consultation1, consultation2) -> consultation1.getStartDate().compareTo(consultation2.getStartDate()))
                .map(ConsultationResponse::fromEntity)
                .toList();
    }

    @Override
    public List<ConsultationResponse> findAllByDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        var startDate = date.atStartOfDay();
        var finalDate = date.atTime(LocalTime.MAX);

        return consultationRepository.findAllByDate(startDate, finalDate)
                .stream()
                .map(ConsultationResponse::fromEntity)
                .toList();
    }

    @Override
    public ConsultationResponse create(ConsultationRequest consultationRequest) {
        if (consultationRequest.startDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("A data de inicio da consulta deve ser futura");
        }

        var finalDate = consultationRequest.startDate().plusHours(consultationRequest.timeDuration().getHour())
                .plusMinutes(consultationRequest.timeDuration().getMinute());

        var patient = userService.findByIdUsers(consultationRequest.patientId());
        var medic = userService.findByIdUsers(consultationRequest.medicId());
        if (!medic.getTypeUserRole().name().equals("MEDIC")) {
            throw new BadRequestException("O usuário deve ser um médico");
        }
        if (medic.getId().equals(patient.getId())) {
            throw new BadRequestException("O médico não pode ser o mesmo que o paciente");
        }

        // Verifica conflito de datas
        if(consultationRepository.findConflictingConsultation(
                medic.getId(),
                consultationRequest.startDate(),
                finalDate
        ).isPresent()) {
            throw new ConflictRequestException("Esse médico possui uma consulta nessa data e horario");
        }

        var consultation = Consultation.create(
                medic,
                patient,
                consultationRequest.startDate(),
                finalDate,
                consultationRequest.description()
        );

        return ConsultationResponse.fromEntity(consultationRepository.save(consultation));
    }

    @Override
    public ConsultationResponse update(UUID id, ConsultationRequest consultationRequest) {
        var consultation = consultationRepository.findById(id);
        if (consultation.isEmpty()) {
            throw new BadRequestException("Consulta não encontrada");
        }
        if (consultation.get().getStatus() == ConsultationStatusEnum.CANCELLED || consultation.get().getStatus() == ConsultationStatusEnum.CONFIRMED) {
            throw new BadRequestException("Não é possível alterar uma consulta que já foi confirmada ou cancelada");
        }

        verifyChanges(consultation.get(), consultationRequest);

        var patient = userService.findByIdUsers(consultationRequest.patientId());
        var medic = userService.findByIdUsers(consultationRequest.medicId());
        if (!medic.getTypeUserRole().name().equals("MEDIC")) {
            throw new BadRequestException("O usuário deve ser um médico");
        }
        if (medic.getId().equals(patient.getId())) {
            throw new BadRequestException("O médico não pode ser o mesmo que o paciente");
        }

        // Verifica conflito de datas
        var conflict = consultationRepository.findConflictingConsultation(
                medic.getId(),
                consultationRequest.startDate(),
                consultation.get().getFinalDate()
        );
        if(conflict.isPresent() && !conflict.get().getId().equals(id)) {
            throw new ConflictRequestException("Esse médico possui uma consulta nessa data e horario");
        }

        var consultationSave = Consultation.create(
                consultation.get().getMedic(),
                consultation.get().getPatient(),
                consultation.get().getStartDate(),
                consultation.get().getFinalDate(),
                consultation.get().getDescription()
        );

        return ConsultationResponse.fromEntity(consultationRepository.save(consultationSave));
    }

    @Override
    public ConsultationResponse confirm(UUID id) {
        var user = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = userService.findByEmailUsers(user);

        var consultation = consultationRepository.findById(id);
        if (consultation.isEmpty()) {
            throw new BadRequestException("Consulta não encontrada");
        }
        if (!users.getId().equals(consultation.get().getPatient().getId())) {
            throw new BadRequestException("Apenas o paciente pode confirmar uma consulta");
        }
        if (consultation.get().getStatus() == ConsultationStatusEnum.CANCELLED) {
            throw new BadRequestException("Não é possível confirmar uma consulta cancelada");
        }
        consultation.get().setStatus(ConsultationStatusEnum.CONFIRMED);
        return ConsultationResponse.fromEntity(consultationRepository.save(consultation.get()));
    }

    @Override
    public void cancel(UUID id) {
        var user = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = userService.findByEmailUsers(user);

        var consultation = consultationRepository.findById(id);
        if (consultation.isEmpty()) {
            throw new BadRequestException("Consulta não encontrada");
        }
        if (!users.getId().equals(consultation.get().getPatient().getId())) {
            throw new BadRequestException("Apenas o paciente pode cancelar uma consulta");
        }
        consultation.get().setStatus(ConsultationStatusEnum.CANCELLED);
        consultationRepository.save(consultation.get());
    }

    private void verifyChanges(Consultation consultation, ConsultationRequest consultationRequest) {
        if (consultationRequest.startDate() != consultation.getStartDate()) {
            consultation.setStartDate(consultationRequest.startDate());
        }
        if (consultationRequest.startDate().plusHours(consultationRequest.timeDuration().getHour())
                .plusMinutes(consultationRequest.timeDuration().getMinute()) != consultation.getFinalDate()) {
            consultation.setFinalDate(consultationRequest.startDate().plusHours(consultationRequest.timeDuration().getHour())
                .plusMinutes(consultationRequest.timeDuration().getMinute()));
        }
        if (!Objects.equals(consultationRequest.description(), consultation.getDescription())) {
            consultation.setDescription(consultationRequest.description());
        }
        if (consultationRequest.medicId() != consultation.getMedic().getId()) {
            consultation.setMedic(userService.findByIdUsers(consultationRequest.medicId()));
        }
        if (consultationRequest.patientId() != consultation.getPatient().getId()) {
            consultation.setPatient(userService.findByIdUsers(consultationRequest.patientId()));
        }
    }
}