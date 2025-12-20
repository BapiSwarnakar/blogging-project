package com.stech.authentication.dto.response;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    Long id; 
    String name; 
    String email; 
    Set<String> permissions;
}
