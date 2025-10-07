package com.fiap.itmoura.consultation_service.consultation.application.usecases.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fiap.itmoura.consultation_service.consultation.application.dto.request.ConsultationKafkaRequest;
import com.fiap.itmoura.consultation_service.consultation.application.dto.response.ConsultationResponse;
import com.fiap.itmoura.consultation_service.consultation.domain.Consultation;
import com.fiap.itmoura.consultation_service.consultation.infrastructure.producer.ConsultationKafkaProducer;
import com.fiap.itmoura.consultation_service.user.application.domain.UserDTO;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ConsultationSaveAspect {

    private final ConsultationKafkaProducer producer;

    @AfterReturning(pointcut = "execution(* com.fiap.itmoura.consultation_service.consultation.infrastructure.persistence.ConsultationRepository.save(..))",
            returning = "result")
    public void afterConsultationSave(Object result) throws JsonProcessingException {
        if (result != null) {
            Consultation consultation = (Consultation) result;
            ConsultationKafkaRequest consultationKafkaRequest = new ConsultationKafkaRequest(
                    consultation.getId(),
                    UserDTO.fromEntity(consultation.getMedic()),
                    UserDTO.fromEntity(consultation.getPatient()),
                    consultation.getStartDate(),
                    consultation.getFinalDate(),
                    consultation.getDescription(),
                    consultation.getStatus()
            );
            producer.sendKafkaMessage(consultationKafkaRequest);
        }
    }

}
