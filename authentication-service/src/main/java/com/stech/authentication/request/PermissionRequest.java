package com.stech.authentication.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionRequest {
    @NotBlank
    private String name;
    
    private String description;
}
