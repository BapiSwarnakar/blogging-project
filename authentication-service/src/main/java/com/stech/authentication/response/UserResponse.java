package com.stech.authentication.response;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    Long id; 
    String username; 
    String email; 
    Set<String> permissions;
}
