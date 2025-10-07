package com.fiap.itmoura.consultation_service.consultation.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fiap.itmoura.consultation_service.consultation.domain.enums.ConsultationStatusEnum;
import com.fiap.itmoura.consultation_service.user.application.domain.UserDTO;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultationKafkaRequest(

    UUID id,

    @NotNull(message = "Medic ID is required")
    UserDTO medicId,

    @NotNull(message = "Patient ID is required")
    UserDTO patientId,

    @NotNull(message = "startDate is required")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime startDate,

    @NotNull(message = "Time duration is required")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime finalDate,

    String description,

    ConsultationStatusEnum status
) {
}