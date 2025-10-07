package com.fiap.itmoura.consultation_service.consultation.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsultationStatusEnumTest {

    @Test
    void shouldHaveCorrectValues() {
        assertEquals("SCHEDULED", ConsultationStatusEnum.SCHEDULED.getStatus());
        assertEquals("CONFIRMED", ConsultationStatusEnum.CONFIRMED.getStatus());
        assertEquals("CANCELLED", ConsultationStatusEnum.CANCELLED.getStatus());
        assertEquals("COMPLETED", ConsultationStatusEnum.COMPLETED.getStatus());
    }

    @Test
    void shouldHaveAllExpectedValues() {
        ConsultationStatusEnum[] values = ConsultationStatusEnum.values();
        assertEquals(4, values.length);
        
        assertTrue(containsValue(values, ConsultationStatusEnum.SCHEDULED));
        assertTrue(containsValue(values, ConsultationStatusEnum.CONFIRMED));
        assertTrue(containsValue(values, ConsultationStatusEnum.CANCELLED));
        assertTrue(containsValue(values, ConsultationStatusEnum.COMPLETED));
    }

    @Test
    void shouldConvertFromString() {
        assertEquals(ConsultationStatusEnum.SCHEDULED, ConsultationStatusEnum.valueOf("SCHEDULED"));
        assertEquals(ConsultationStatusEnum.CONFIRMED, ConsultationStatusEnum.valueOf("CONFIRMED"));
        assertEquals(ConsultationStatusEnum.CANCELLED, ConsultationStatusEnum.valueOf("CANCELLED"));
        assertEquals(ConsultationStatusEnum.COMPLETED, ConsultationStatusEnum.valueOf("COMPLETED"));
    }

    private boolean containsValue(ConsultationStatusEnum[] values, ConsultationStatusEnum target) {
        for (ConsultationStatusEnum value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }
}
