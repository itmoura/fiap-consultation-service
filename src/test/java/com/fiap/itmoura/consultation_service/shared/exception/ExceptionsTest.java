package com.fiap.itmoura.consultation_service.shared.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    void shouldCreateBadRequestExceptionWithMessage() {
        String message = "Bad request error";
        BadRequestException exception = new BadRequestException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreateBadRequestExceptionWithMessageAndCause() {
        String message = "Bad request error";
        Throwable cause = new RuntimeException("Root cause");
        BadRequestException exception = new BadRequestException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldCreateConflictRequestExceptionWithMessage() {
        String message = "Conflict error";
        ConflictRequestException exception = new ConflictRequestException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreateConflictRequestExceptionWithMessageAndCause() {
        String message = "Conflict error";
        Throwable cause = new RuntimeException("Root cause");
        ConflictRequestException exception = new ConflictRequestException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldCreateForbiddenExceptionWithMessage() {
        String message = "Forbidden error";
        ForbiddenException exception = new ForbiddenException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreateForbiddenExceptionWithMessageAndCause() {
        String message = "Forbidden error";
        Throwable cause = new RuntimeException("Root cause");
        ForbiddenException exception = new ForbiddenException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldInheritFromRuntimeException() {
        BadRequestException badRequestException = new BadRequestException("test");
        ConflictRequestException conflictException = new ConflictRequestException("test");
        ForbiddenException forbiddenException = new ForbiddenException("test");

        assertTrue(badRequestException instanceof RuntimeException);
        assertTrue(conflictException instanceof RuntimeException);
        assertTrue(forbiddenException instanceof RuntimeException);
    }
}
