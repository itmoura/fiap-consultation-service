package com.fiap.itmoura.consultation_service.user.application.service;

import com.fiap.itmoura.consultation_service.shared.exception.BadRequestException;
import com.fiap.itmoura.consultation_service.shared.exception.ConflictRequestException;
import com.fiap.itmoura.consultation_service.user.application.domain.UserDTO;
import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import com.fiap.itmoura.consultation_service.user.domain.Users;
import com.fiap.itmoura.consultation_service.user.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Users testUser;
    private UserDTO testUserDTO;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testUser = Users.builder()
                .id(testId)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .typeUserRole(TypeUserEnum.PATIENT)
                .isActive(true)
                .build();

        testUserDTO = new UserDTO(
                testId,
                "Test User",
                "test@example.com",
                "password123",
                TypeUserEnum.PATIENT,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldFindAllActiveUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDTO> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void shouldFindAllPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Users> userPage = new PageImpl<>(List.of(testUser));
        when(userRepository.findByIsActiveTrue(pageable)).thenReturn(userPage);

        Page<UserDTO> result = userService.findAllPaginated(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findByIsActiveTrue(pageable);
    }

    @Test
    void shouldFindUserById() {
        when(userRepository.findByIdAndIsActiveTrue(testId)).thenReturn(Optional.of(testUser));

        UserDTO result = userService.findById(testId);

        assertNotNull(result);
        assertEquals(testUser.getName(), result.name());
        verify(userRepository).findByIdAndIsActiveTrue(testId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByIdAndIsActiveTrue(testId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> userService.findById(testId));
        verify(userRepository).findByIdAndIsActiveTrue(testId);
    }

    @Test
    void shouldFindUserByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDTO result = userService.findByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.email());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        UserDTO result = userService.create(testUserDTO);

        assertNotNull(result);
        assertEquals(testUser.getName(), result.name());
        verify(userRepository).existsByEmail(testUserDTO.email());
        verify(passwordEncoder).encode(testUserDTO.password());
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictRequestException.class, () -> userService.create(testUserDTO));
        verify(userRepository).existsByEmail(testUserDTO.email());
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void shouldUpdateUser() {
        UserDTO updateDTO = new UserDTO(
                testId,
                "Updated Name",
                "updated@example.com",
                "newPassword",
                TypeUserEnum.MEDIC,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        UserDTO result = userService.update(testId, updateDTO);

        assertNotNull(result);
        verify(userRepository).findById(testId);
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        userService.delete(testId);

        verify(userRepository).findById(testId);
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void shouldActivateUser() {
        testUser.setIsActive(false);
        when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        userService.activate(testId);

        verify(userRepository).findById(testId);
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void shouldThrowExceptionWhenCurrentPasswordIsWrong() {
        when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        assertThrows(BadRequestException.class, 
            () -> userService.changePassword(testId, "wrongPassword", "newPassword"));
        
        verify(passwordEncoder).matches("wrongPassword", testUser.getPassword());
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void shouldCountActiveUsers() {
        when(userRepository.countByIsActiveTrue()).thenReturn(5L);

        long result = userService.countActiveUsers();

        assertEquals(5L, result);
        verify(userRepository).countByIsActiveTrue();
    }

    @Test
    void shouldCountUsersByType() {
        when(userRepository.countByTypeUserRoleAndIsActiveTrue(TypeUserEnum.PATIENT)).thenReturn(3L);

        long result = userService.countUsersByType(TypeUserEnum.PATIENT);

        assertEquals(3L, result);
        verify(userRepository).countByTypeUserRoleAndIsActiveTrue(TypeUserEnum.PATIENT);
    }
}
