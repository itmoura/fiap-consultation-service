package com.fiap.itmoura.consultation_service.shared.application.handlers;

import com.fiap.itmoura.consultation_service.shared.domain.ValidationErrorDTO;
import com.fiap.itmoura.consultation_service.shared.exception.BadRequestException;
import com.fiap.itmoura.consultation_service.shared.exception.ConflictRequestException;
import com.fiap.itmoura.consultation_service.shared.exception.ForbiddenException;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDTO> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var status = HttpStatus.BAD_REQUEST;
        List<String> errors = new ArrayList<>();
        for (var error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        return ResponseEntity
                .status(status.value())
                .body(new ValidationErrorDTO(errors, status.value()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ValidationErrorDTO> handlerBadRequestException(BadRequestException ex) {
        var status = HttpStatus.BAD_REQUEST;
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        return ResponseEntity
                .status(status.value())
                .body(new ValidationErrorDTO(errors, status.value()));
    }

    @ExceptionHandler(ConflictRequestException.class)
    public ResponseEntity<ValidationErrorDTO> handlerConflictRequestException(ConflictRequestException ex) {
        var status = HttpStatus.CONFLICT;
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        return ResponseEntity
                .status(status.value())
                .body(new ValidationErrorDTO(errors, status.value()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ValidationErrorDTO> handlerForbiddenException(ForbiddenException ex) {
        var status = HttpStatus.FORBIDDEN;
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        return ResponseEntity
                .status(status.value())
                .body(new ValidationErrorDTO(errors, status.value()));
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ValidationErrorDTO> handlerPSQLException(PSQLException ex) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        List<String> errors = new ArrayList<>();
        errors.add("Database error: " + ex.getMessage());

        return ResponseEntity
                .status(status.value())
                .body(new ValidationErrorDTO(errors, status.value()));
    }

}
