package com.fiap.itmoura.consultation_service.user.infrastructure.persistence;

import com.fiap.itmoura.consultation_service.user.application.domain.enums.TypeUserEnum;
import com.fiap.itmoura.consultation_service.user.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    
    Optional<Users> findByEmail(String email);

    Optional<Users> findByIdAndIsActiveTrue(UUID id);
    
    boolean existsByEmail(String email);
    
    Page<Users> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT u FROM users u WHERE u.typeUserRole = :typeUserId AND u.isActive = true")
    List<Users> findByTypeUserRoleAndIsActiveTrue(@Param("typeUserRole") TypeUserEnum typeUserRole);
    
    long countByIsActiveTrue();
    
    @Query("SELECT COUNT(u) FROM users u WHERE u.typeUserRole = :typeUserId AND u.isActive = true")
    long countByTypeUserRoleAndIsActiveTrue(@Param("typeUserRole") TypeUserEnum typeUserRole);
    
    @Query("SELECT u FROM users u WHERE u.isActive = true AND LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Users> findByNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name);
    
    @Query("SELECT u FROM users u WHERE u.isActive = true AND u.email = :email")
    Optional<Users> findByEmailAndIsActiveTrue(@Param("email") String email);
}
