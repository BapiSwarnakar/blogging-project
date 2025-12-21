package com.stech.authentication.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleRequest {
    @NotBlank
    private String name;
    private String description;
    private Set<Long> permissionId;
    @Builder.Default
    private boolean isActive = true;
    @Builder.Default
    private boolean isFullAccess = false;
}
