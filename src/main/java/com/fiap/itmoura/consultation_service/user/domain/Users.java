package com.fiap.itmoura.consultation_service.user.domain;

import com.fiap.itmoura.consultation_service.user.application.domain.UserDTO;
import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "users")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeUserEnum typeUserRole;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastUpdatedAt;

    @Column
    @Builder.Default
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
    }


    public static Users fromDTO(UserDTO userDTO) {
        Users users = new Users();
        users.setId(userDTO.id());
        users.setName(users.getName());
        users.setEmail(users.getEmail());

        return users;
    }
}
