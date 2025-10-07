package com.fiap.itmoura.consultation_service.consultation.application.dto.request;

import com.fiap.itmoura.consultation_service.consultation.domain.enums.ConsultationStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record ConsultationSearchRequest(

        @Schema(description = "ID do médico", example = "c72cad86-2a08-46a6-8576-6277a0998db9")
        UUID medicId,

        @Schema(description = "ID do paciente", example = "c72cad86-2a08-46a6-8576-6277a0998db9")
        UUID patientId,

        @Schema(description = "Data da consulta", example = "2023-10-10")
        LocalDate date,

        @Schema(description = "Status da consulta", example = "PENDING")
        ConsultationStatusEnum status
) {
}
