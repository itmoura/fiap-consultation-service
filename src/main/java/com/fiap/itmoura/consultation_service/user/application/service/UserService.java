package com.fiap.itmoura.consultation_service.user.application.service;

import com.fiap.itmoura.consultation_service.shared.exception.BadRequestException;
import com.fiap.itmoura.consultation_service.shared.exception.ConflictRequestException;
import com.fiap.itmoura.consultation_service.user.application.domain.UserDTO;
import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import com.fiap.itmoura.consultation_service.user.domain.Users;
import com.fiap.itmoura.consultation_service.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public List<UserDTO> findAll() {
        log.info("Buscando todos os usuários ativos");
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getIsActive())
                .map(UserDTO::fromEntity)
                .toList();
    }

    public Page<UserDTO> findAllPaginated(Pageable pageable) {
        log.info("Buscando usuários paginados - página: {}, tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findByIsActiveTrue(pageable)
                .map(UserDTO::fromEntity);
    }

    public UserDTO findById(UUID id) {
        log.info("Buscando usuário por ID: {}", id);
        Users user = findByIdUsers(id);
        
        return UserDTO.fromEntity(user);
    }

    public Users findByIdUsers(UUID id) {
        Users user = userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        return user;
    }

    public UserDTO findByEmail(String email) {
        log.info("Buscando usuário por email: {}", email);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));
        
        if (!user.getIsActive()) {
            throw new BadRequestException("Usuário não está ativo");
        }
        
        return UserDTO.fromEntity(user);
    }

    public Users findByEmailUsers(String email) {
        log.info("Buscando usuário por email: {}", email);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        if (!user.getIsActive()) {
            throw new BadRequestException("Usuário não está ativo");
        }

        return user;
    }

    public List<UserDTO> findByTypeUserRole(TypeUserEnum typeUserEnum) {
        log.info("Buscando usuários por tipo de usuário: {}", typeUserEnum);
        return userRepository.findByTypeUserRoleAndIsActiveTrue(typeUserEnum)
                .stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    public UserDTO create(UserDTO userDTO) {
        log.info("Criando novo usuário com email: {}", userDTO.email());
        
        // Validações
        if (userRepository.existsByEmail(userDTO.email())) {
            throw new ConflictRequestException("Já existe um usuário com este email");
        }

        Users user = Users.builder()
                .name(userDTO.name())
                .email(userDTO.email())
                .password(encoder.encode(userDTO.password()))
                .typeUserRole(userDTO.typeUserRole())
                .isActive(true)
                .build();

        Users savedUser = userRepository.save(user);
        log.info("Usuário criado com sucesso: {}", savedUser.getEmail());
        return UserDTO.fromEntity(savedUser);
    }

    public UserDTO update(UUID id, UserDTO userDTO) {
        log.info("Atualizando usuário com ID: {}", id);
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        if (!user.getIsActive()) {
            throw new BadRequestException("Não é possível atualizar um usuário inativo");
        }

        // Validações de unicidade
        if (!user.getEmail().equals(userDTO.email()) && userRepository.existsByEmail(userDTO.email())) {
            throw new ConflictRequestException("Já existe um usuário com este email");
        }

        // Atualiza campos
        if (Objects.nonNull(userDTO.name()) && !userDTO.name().equals(user.getName())) {
            user.setName(userDTO.name());
        }

        if (Objects.nonNull(userDTO.email()) && !userDTO.email().equals(user.getEmail())) {
            user.setEmail(userDTO.email());
        }

        if (Objects.nonNull(userDTO.password())) {
            user.setPassword(encoder.encode(userDTO.password()));
        }

        if (Objects.nonNull(userDTO.typeUserRole())) {
            user.setTypeUserRole(userDTO.typeUserRole());
        }

        Users updatedUser = userRepository.save(user);
        log.info("Usuário atualizado com sucesso: {}", updatedUser.getEmail());
        return UserDTO.fromEntity(updatedUser);
    }

    public void delete(UUID id) {
        log.info("Desativando usuário com ID: {}", id);
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        user.setIsActive(false);
        userRepository.save(user);
        log.info("Usuário desativado com sucesso: {}", user.getEmail());
    }

    public void activate(UUID id) {
        log.info("Ativando usuário com ID: {}", id);
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        user.setIsActive(true);
        userRepository.save(user);
        log.info("Usuário ativado com sucesso: {}", user.getEmail());
    }

    public UserDTO changePassword(UUID id, String currentPassword, String newPassword) {
        log.info("Alterando senha do usuário com ID: {}", id);
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        if (!user.getIsActive()) {
            throw new BadRequestException("Usuário não está ativo");
        }

        if (!encoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("Senha atual incorreta");
        }

        user.setPassword(encoder.encode(newPassword));
        Users updatedUser = userRepository.save(user);
        log.info("Senha alterada com sucesso para usuário: {}", user.getEmail());
        return UserDTO.fromEntity(updatedUser);
    }

    public long countActiveUsers() {
        log.info("Contando usuários ativos");
        return userRepository.countByIsActiveTrue();
    }

    public long countUsersByType(TypeUserEnum typeUserRole) {
        log.info("Contando usuários por tipo: {}", typeUserRole);
        return userRepository.countByTypeUserRoleAndIsActiveTrue(typeUserRole);
    }
}
