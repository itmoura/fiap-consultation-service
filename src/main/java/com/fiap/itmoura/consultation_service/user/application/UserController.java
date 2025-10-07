package com.fiap.itmoura.consultation_service.user.application;

import com.fiap.itmoura.consultation_service.user.application.domain.OnCreate;
import com.fiap.itmoura.consultation_service.user.application.domain.OnUpdate;
import com.fiap.itmoura.consultation_service.user.application.domain.UserDTO;
import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import com.fiap.itmoura.consultation_service.user.application.interfaces.UserControllerInterface;
import com.fiap.itmoura.consultation_service.user.application.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerInterface {

    private final UserService userService;

    @Override
    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @Override
    @GetMapping("/paginated")
    public Page<UserDTO> findAllPaginated(
            @PageableDefault(size = 20) Pageable pageable) {
        return userService.findAllPaginated(pageable);
    }

    @Override
    @GetMapping("/{id}")
    public UserDTO findById(
            @Parameter(description = "ID do usuário") @PathVariable UUID id) {
        return userService.findById(id);
    }

    @Override
    @GetMapping("/email/{email}")
    public UserDTO findByEmail(
            @Parameter(description = "Email do usuário") @PathVariable String email) {
        return userService.findByEmail(email);
    }

    @Override
    @GetMapping("/type/{typeUserRole}")
    public List<UserDTO> findByTypeUserRole(
            @Parameter(description = "ID do tipo de usuário") @PathVariable TypeUserEnum typeUserRole) {
        return userService.findByTypeUserRole(typeUserRole);
    }

    @Override
    @PostMapping
    public UserDTO create(
            @Validated(OnCreate.class) @RequestBody UserDTO userDTO) {
        return userService.create(userDTO);
    }

    @Override
    @PutMapping("/{id}")
    public UserDTO update(
            @Parameter(description = "ID do usuário") @PathVariable UUID id,
            @Validated(OnUpdate.class) @RequestBody UserDTO userDTO) {
        return userService.update(id, userDTO);
    }
    
    @Override
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "ID do usuário") @PathVariable UUID id) {
        userService.delete(id);
    }

    @Override
    @PatchMapping("/{id}/activate")
    public void activate(
            @Parameter(description = "ID do usuário") @PathVariable UUID id) {
        userService.activate(id);
    }

    @Override
    @PatchMapping("/{id}/change-password")
    public UserDTO changePassword(
            @Parameter(description = "ID do usuário") @PathVariable UUID id,
            @Parameter(description = "Senha atual") @RequestParam String currentPassword,
            @Parameter(description = "Nova senha") @RequestParam String newPassword) {
        return userService.changePassword(id, currentPassword, newPassword);
    }

    @Override
    @GetMapping("/count")
    public Long countActiveUsers() {
        return userService.countActiveUsers();
    }

    @Override
    @GetMapping("/count/type/{typeUserRole}")
    public Long countUsersByType(
            @Parameter(description = "ID do tipo de usuário") @PathVariable TypeUserEnum typeUserRole) {
        return userService.countUsersByType(typeUserRole);
    }
}
