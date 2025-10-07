package com.fiap.itmoura.consultation_service.shared.exception;

public class ConflictRequestException extends RuntimeException {
    public ConflictRequestException(String message) {
        super(message);
    }

    public ConflictRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
