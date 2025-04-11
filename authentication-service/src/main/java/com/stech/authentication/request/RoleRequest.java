package com.stech.authentication.request;

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
    
    private Set<String> permissionNames;
}
