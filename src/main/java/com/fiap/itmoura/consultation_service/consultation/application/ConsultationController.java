package com.fiap.itmoura.consultation_service.consultation.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fiap.itmoura.consultation_service.consultation.application.dto.request.ConsultationRequest;
import com.fiap.itmoura.consultation_service.consultation.application.dto.request.ConsultationSearchRequest;
import com.fiap.itmoura.consultation_service.consultation.application.dto.response.ConsultationResponse;
import com.fiap.itmoura.consultation_service.consultation.application.interfaces.ConsultationInterface;
import com.fiap.itmoura.consultation_service.consultation.application.usecases.ConsultationService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
public class ConsultationController implements ConsultationInterface {

    private final ConsultationService consultationService;

    @Override
    public List<ConsultationResponse> findAll() {
        return consultationService.findAll();
    }

    @Override
    public List<ConsultationResponse> findAllByDate(
            @Parameter(description = "Data da consulta (opcional)")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date
    ) {
        return consultationService.findAllByDate(date);
    }

    @Override
    public ConsultationResponse create(ConsultationRequest consultationRequest) {
        return consultationService.create(consultationRequest);
    }

    @Override
    public ConsultationResponse update(UUID id, ConsultationRequest consultationRequest) {
        return consultationService.update(id, consultationRequest);
    }

    @Override
    public ConsultationResponse confirm(UUID id) {
        return consultationService.confirm(id);
    }

    @Override
    public void cancel(UUID id) {
        consultationService.cancel(id);
    }
}