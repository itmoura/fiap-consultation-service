package com.fiap.itmoura.consultation_service.shared.domain;

import java.util.List;

public record ValidationErrorDTO(List<String> errors, int status) {
}
