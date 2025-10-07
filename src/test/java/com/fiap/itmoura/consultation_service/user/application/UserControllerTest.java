package com.fiap.itmoura.consultation_service.user.application;

import com.fiap.itmoura.consultation_service.user.application.domain.UserDTO;
import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import com.fiap.itmoura.consultation_service.user.application.service.UserService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO testUserDTO;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
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
    void shouldFindAllUsers() {
        when(userService.findAll()).thenReturn(List.of(testUserDTO));

        List<UserDTO> result = userController.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserDTO.name(), result.get(0).name());
        verify(userService).findAll();
    }

    @Test
    void shouldFindAllUsersPaginated() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<UserDTO> userPage = new PageImpl<>(List.of(testUserDTO));
        when(userService.findAllPaginated(any(Pageable.class))).thenReturn(userPage);

        Page<UserDTO> result = userController.findAllPaginated(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUserDTO.name(), result.getContent().get(0).name());
        verify(userService).findAllPaginated(any(Pageable.class));
    }

    @Test
    void shouldFindUserById() {
        when(userService.findById(testId)).thenReturn(testUserDTO);

        UserDTO result = userController.findById(testId);

        assertNotNull(result);
        assertEquals(testUserDTO.name(), result.name());
        assertEquals(testUserDTO.email(), result.email());
        verify(userService).findById(testId);
    }

    @Test
    void shouldFindUserByEmail() {
        String email = "test@example.com";
        when(userService.findByEmail(email)).thenReturn(testUserDTO);

        UserDTO result = userController.findByEmail(email);

        assertNotNull(result);
        assertEquals(testUserDTO.name(), result.name());
        assertEquals(testUserDTO.email(), result.email());
        verify(userService).findByEmail(email);
    }

    @Test
    void shouldCreateUser() {
        when(userService.create(any(UserDTO.class))).thenReturn(testUserDTO);

        UserDTO result = userController.create(testUserDTO);

        assertNotNull(result);
        assertEquals(testUserDTO.name(), result.name());
        verify(userService).create(any(UserDTO.class));
    }

    @Test
    void shouldUpdateUser() {
        when(userService.update(eq(testId), any(UserDTO.class))).thenReturn(testUserDTO);

        UserDTO result = userController.update(testId, testUserDTO);

        assertNotNull(result);
        assertEquals(testUserDTO.name(), result.name());
        verify(userService).update(eq(testId), any(UserDTO.class));
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(userService).delete(testId);

        userController.delete(testId);

        verify(userService).delete(testId);
    }

    @Test
    void shouldActivateUser() {
        doNothing().when(userService).activate(testId);

        userController.activate(testId);

        verify(userService).activate(testId);
    }

    @Test
    void shouldFindUsersByType() {
        when(userService.findByTypeUserRole(TypeUserEnum.PATIENT)).thenReturn(List.of(testUserDTO));

        List<UserDTO> result = userController.findByTypeUserRole(TypeUserEnum.PATIENT);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserDTO.typeUserRole(), result.get(0).typeUserRole());
        verify(userService).findByTypeUserRole(TypeUserEnum.PATIENT);
    }
}
