package com.stech.usermgmt.dto.response;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupResponse {
    Long id; 
    String name; 
    String email; 
    Set<String> permissions;
}
