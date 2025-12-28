package com.stech.authentication.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT DISTINCT u FROM UserEntity u " +
           "LEFT JOIN FETCH u.roles r " +
           "LEFT JOIN FETCH r.permissions " +
           "LEFT JOIN FETCH u.directPermissions " +
           "WHERE u.email = :email")
    Optional<UserEntity> findByEmailWithRolesAndPermissions(String email);

    @Query("SELECT DISTINCT p FROM UserEntity u " +
           "JOIN u.roles r " +
           "JOIN r.permissions p " +
           "WHERE u.id = :userId")
    Set<PermissionEntity> findPermissionsByRoles(@Param("userId") Long userId);

    @Query("SELECT p FROM UserEntity u " +
           "JOIN u.directPermissions p " +
           "WHERE u.id = :userId")
    Set<PermissionEntity> findDirectPermissions(@Param("userId") Long userId);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE " +
           "(:search IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<UserEntity> findAllWithSearch(@Param("search") String search, Pageable pageable);

//     Optional<RefreshTokenEntity> findByEmail(String email);
    
}
