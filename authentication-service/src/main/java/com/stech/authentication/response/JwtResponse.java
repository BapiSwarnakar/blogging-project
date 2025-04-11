package com.stech.authentication.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    String token;
    Long id;
    String username;
    String email;
    List<String> permissions;
}

