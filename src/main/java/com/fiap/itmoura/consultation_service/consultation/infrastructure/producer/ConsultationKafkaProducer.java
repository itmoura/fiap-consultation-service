package com.fiap.itmoura.consultation_service.consultation.infrastructure.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.itmoura.consultation_service.consultation.application.dto.request.ConsultationKafkaRequest;
import com.fiap.itmoura.consultation_service.consultation.domain.Consultation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class ConsultationKafkaProducer {

    @Value("${kafka.producer.scheduling}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendKafkaMessage(ConsultationKafkaRequest consultationKafkaRequest) throws JsonProcessingException {
        log.info("Sending message to Kafka");
        var messagePayload = new ObjectMapper().writeValueAsString(consultationKafkaRequest);
        kafkaTemplate.send(topic, messagePayload);
    }
}
