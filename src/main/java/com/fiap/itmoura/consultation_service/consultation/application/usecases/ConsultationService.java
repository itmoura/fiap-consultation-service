package com.fiap.itmoura.consultation_service.consultation.application.usecases;

import com.fiap.itmoura.consultation_service.consultation.application.dto.request.ConsultationRequest;
import com.fiap.itmoura.consultation_service.consultation.application.dto.response.ConsultationResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ConsultationService {

    List<ConsultationResponse> findAll();
    List<ConsultationResponse> findAllByDate(LocalDate date);
    ConsultationResponse create(ConsultationRequest consultationRequest);
    ConsultationResponse update(UUID id, ConsultationRequest consultationRequest);
    ConsultationResponse confirm(UUID id);
    void cancel(UUID id);
}
