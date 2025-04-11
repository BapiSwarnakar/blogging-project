package com.stech.authentication.request;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionValidationRequest {
    String token;
    List<String> requiredPermissions;
}
