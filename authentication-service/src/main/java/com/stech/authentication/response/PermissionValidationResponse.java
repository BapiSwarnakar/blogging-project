package com.stech.authentication.response;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionValidationResponse {
    boolean isValid;
    String message;
    Set<String> userPermissions;
}
