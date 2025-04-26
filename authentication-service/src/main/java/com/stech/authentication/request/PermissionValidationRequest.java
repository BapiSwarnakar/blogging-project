package com.stech.authentication.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionValidationRequest {
    String token;
    String requiredPermissionsApi;
    String requiredPermissionsMethod;
    String ipAddress;
}
