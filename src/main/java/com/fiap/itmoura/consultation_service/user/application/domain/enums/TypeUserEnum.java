package com.fiap.itmoura.consultation_service.user.application.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TypeUserEnum {

    MEDIC("MEDIC"),
    PATIENT("PATIENT"),
    NURSE("NURSE"),
    ADMIN("ADMIN");

    private final String type;
}
