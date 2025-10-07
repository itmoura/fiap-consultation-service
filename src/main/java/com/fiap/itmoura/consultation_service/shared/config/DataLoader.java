package com.fiap.itmoura.consultation_service.shared.config;

import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import com.fiap.itmoura.consultation_service.user.domain.Users;
import com.fiap.itmoura.consultation_service.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        log.info("Iniciando carregamento de dados iniciais...");

        createAdminUser();
        
        log.info("Carregamento de dados iniciais concluído!");
    }

    private void createAdminUser() {
        log.info("Criando usuário administrador padrão...");
        
        String adminEmail = "admin@sistema.com";
        
        if (!userRepository.existsByEmail(adminEmail)) {
            Users adminUser = Users.builder()
                    .name("Administrador do Sistema")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .typeUserRole(TypeUserEnum.ADMIN)
                    .isActive(true)
                    .build();
                
            userRepository.save(adminUser);
            log.info("Usuário administrador criado com sucesso!");
            log.info("Email: {} | Senha: admin123", adminEmail);
        } else {
            log.info("Usuário administrador já existe");
        }
    }
}
