package com.fiap.itmoura.consultation_service.user.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import com.fiap.itmoura.consultation_service.user.domain.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDTO(

        @Schema(title = "id", description = "User ID", example = "12345")
        UUID id,

        @Schema(title = "name", description = "User name", example = "John Doe")
        @NotNull( message = "Name is required", groups = OnCreate.class)
        String name,

        @Schema(title = "email", description = "User email", example = "italo@meuemail.com")
        @NotNull(message = "Email is required", groups = OnCreate.class)
        String email,

        @Schema(title = "password", description = "User password", example = "123456")
        @NotNull(message = "Password is required", groups = OnCreate.class)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,

        @Schema(title = "typeUserRole", description = "Type user ROLE", example = "MEDIC")
        @NotNull(message = "Type user is required, please choose one [ADMIN, MEDIC, PATIENT, NURSE]", groups = OnCreate.class)
        TypeUserEnum typeUserRole,

        @Schema(title = "lastUpdate", description = "Last update timestamp", example = "2023-10-01T12:00:00Z")
        @LastModifiedDate
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime lastUpdate,

        @Schema(title = "createdAt", description = "Creation timestamp", example = "2023-10-01T12:00:00Z")
        @CreatedDate
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime createdAt
) {
        public static UserDTO fromEntity(Users users) {
            return new UserDTO(
                    users.getId(),
                    users.getName(),
                    users.getEmail(),
                    users.getPassword(),
                    users.getTypeUserRole(),
                    users.getLastUpdatedAt(),
                    users.getCreatedAt()
            );
        }
}
