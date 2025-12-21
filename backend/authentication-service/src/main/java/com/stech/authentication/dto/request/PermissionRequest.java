package com.stech.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionRequest {
    @NotBlank
    private String name;
    private String category;
    private String slug;
    private String apiUrl;
    private String apiMethod;
    private String description;
}
