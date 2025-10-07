package com.fiap.itmoura.consultation_service.user.application.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeUserEnumTest {

    @Test
    void shouldHaveCorrectValues() {
        assertEquals("MEDIC", TypeUserEnum.MEDIC.getType());
        assertEquals("PATIENT", TypeUserEnum.PATIENT.getType());
        assertEquals("NURSE", TypeUserEnum.NURSE.getType());
        assertEquals("ADMIN", TypeUserEnum.ADMIN.getType());
    }

    @Test
    void shouldHaveAllExpectedValues() {
        TypeUserEnum[] values = TypeUserEnum.values();
        assertEquals(4, values.length);
        
        assertTrue(containsValue(values, TypeUserEnum.MEDIC));
        assertTrue(containsValue(values, TypeUserEnum.PATIENT));
        assertTrue(containsValue(values, TypeUserEnum.NURSE));
        assertTrue(containsValue(values, TypeUserEnum.ADMIN));
    }

    @Test
    void shouldConvertFromString() {
        assertEquals(TypeUserEnum.MEDIC, TypeUserEnum.valueOf("MEDIC"));
        assertEquals(TypeUserEnum.PATIENT, TypeUserEnum.valueOf("PATIENT"));
        assertEquals(TypeUserEnum.NURSE, TypeUserEnum.valueOf("NURSE"));
        assertEquals(TypeUserEnum.ADMIN, TypeUserEnum.valueOf("ADMIN"));
    }

    private boolean containsValue(TypeUserEnum[] values, TypeUserEnum target) {
        for (TypeUserEnum value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }
}
