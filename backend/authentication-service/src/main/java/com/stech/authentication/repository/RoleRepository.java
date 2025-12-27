package com.stech.authentication.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stech.authentication.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String string);
    boolean existsByName(String name);
    Page<RoleEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
