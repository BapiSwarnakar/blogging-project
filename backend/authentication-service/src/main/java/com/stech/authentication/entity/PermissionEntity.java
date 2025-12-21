package com.stech.authentication.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = true)
    private String apiUrl;

    @Column(nullable = true)
    private String apiMethod;

    @Column(nullable = true)
    private String description;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false;

    @Builder.Default
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<RoleEntity> roles = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "directPermissions", fetch = FetchType.LAZY)
    private Set<UserEntity> users = new HashSet<>();
}