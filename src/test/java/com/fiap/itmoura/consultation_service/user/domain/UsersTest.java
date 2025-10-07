package com.fiap.itmoura.consultation_service.user.domain;

import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsersTest {

    @Test
    void shouldCreateUserWithBuilder() {
        UUID id = UUID.randomUUID();
        String name = "Test User";
        String email = "test@example.com";
        String password = "password123";
        TypeUserEnum typeUserRole = TypeUserEnum.PATIENT;

        Users user = Users.builder()
                .id(id)
                .name(name)
                .email(email)
                .password(password)
                .typeUserRole(typeUserRole)
                .isActive(true)
                .build();

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(typeUserRole, user.getTypeUserRole());
        assertTrue(user.getIsActive());
    }

    @Test
    void shouldSetDefaultActiveStatusToTrue() {
        Users user = Users.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .typeUserRole(TypeUserEnum.PATIENT)
                .build();

        assertTrue(user.getIsActive());
    }

    @Test
    void shouldUpdateTimestampOnUpdate() {
        Users user = new Users();
        user.onCreate();
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        user.onUpdate();
        
        assertNotNull(user.getLastUpdatedAt());
    }
}
