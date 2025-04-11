package com.stech.authentication.request;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupRequest {
    String username;
    String email;
    String password;
    Set<String> roles;
    Set<String> directPermissions;
}
