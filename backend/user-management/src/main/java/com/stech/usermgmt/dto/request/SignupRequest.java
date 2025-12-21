package com.stech.usermgmt.dto.request;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupRequest {
    String name;
    String email;
    String password;
    Set<String> roles;
    Set<String> directPermissions;
}
