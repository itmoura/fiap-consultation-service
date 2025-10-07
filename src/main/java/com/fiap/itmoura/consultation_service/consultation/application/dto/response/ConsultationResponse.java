package com.fiap.itmoura.consultation_service.consultation.application.dto.response;

import com.fiap.itmoura.consultation_service.consultation.domain.Consultation;
import com.fiap.itmoura.consultation_service.consultation.domain.enums.ConsultationStatusEnum;
import com.fiap.itmoura.consultation_service.user.application.domain.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationResponse {

    @Schema(title = "id", description = "ID da consulta", example = "12345")
    private UUID id;

    @Schema(title = "medic", description = "Médico da consulta", example = "12345")
    private UserDTO medic;

    @Schema(title = "patient", description = "Paciente da consulta", example = "12345")
    private UserDTO patient;

    @Schema(title = "startDate", description = "Data da consulta", example = "2023-10-10T10:00:00")
    private LocalDateTime startDate;

    @Schema(title = "finalDate", description = "Data final da consulta", example = "2023-10-10T10:30:00")
    private LocalDateTime finalDate;

    @Schema(title = "status", description = "Status da consulta", example = "PENDING")
    private ConsultationStatusEnum status;

    @Schema(title = "description", description = "Descrição da consulta", example = "Consulta de rotina")
    private String description;

    public static ConsultationResponse fromEntity(Consultation consultation) {
        return ConsultationResponse.builder()
                .id(consultation.getId())
                .medic(UserDTO.fromEntity(consultation.getMedic()))
                .patient(UserDTO.fromEntity(consultation.getPatient()))
                .startDate(consultation.getStartDate())
                .finalDate(consultation.getFinalDate())
                .status(consultation.getStatus())
                .description(consultation.getDescription())
                .build();
    }
}