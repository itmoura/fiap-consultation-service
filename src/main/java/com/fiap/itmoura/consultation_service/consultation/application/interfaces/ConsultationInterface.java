package com.fiap.itmoura.consultation_service.consultation.application.interfaces;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fiap.itmoura.consultation_service.consultation.application.dto.request.ConsultationRequest;
import com.fiap.itmoura.consultation_service.consultation.application.dto.request.ConsultationSearchRequest;
import com.fiap.itmoura.consultation_service.consultation.application.dto.response.ConsultationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Consultas", description = "Operações relacionadas às consultas")
@RequestMapping("/api/consultations")
public interface ConsultationInterface {

    @GetMapping
    @PreAuthorize("hasRole('MEDIC') or hasRole('NURSE')")
    @Operation(summary = "Listar todas as consultas", description = "Retorna uma lista com todas as consultas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de consultas retornada com sucesso")
    })
    List<ConsultationResponse> findAll();

    @GetMapping("/today")
    @PreAuthorize("hasRole('MEDIC') or hasRole('NURSE')")
    @Operation(summary = "Listar todas as consultas do dia", description = "Retorna uma lista com todas as consultas do dia")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de consultas do dia retornada com sucesso")
    })
    List<ConsultationResponse> findAllByDate(
            @Parameter(description = "Data da consulta (opcional)")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date
    );
    

    @PostMapping
    @PreAuthorize("hasRole('MEDIC') or hasRole('NURSE')")
    @Operation(summary = "Criar uma nova consulta", description = "Cria uma nova consulta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso")
    })
    ConsultationResponse create(@RequestBody ConsultationRequest consultationRequest);

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEDIC') or hasRole('NURSE')")
    @Operation(summary = "Atualizar uma consulta", description = "Atualiza uma consulta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso")
    })
    ConsultationResponse update(@Parameter(description = "ID da consulta") @PathVariable UUID id, @RequestBody ConsultationRequest consultationRequest);

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirmar uma consulta", description = "Confirma uma consulta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta confirmada com sucesso")
    })
    ConsultationResponse confirm(@Parameter(description = "ID da consulta") @PathVariable UUID id);

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancelar uma consulta", description = "Cancela uma consulta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta cancelada com sucesso")
    })
    void cancel(@Parameter(description = "ID da consulta") @PathVariable UUID id);
    
    
}