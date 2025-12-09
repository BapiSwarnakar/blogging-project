package com.stech.authentication.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn; // Access token expiration in seconds
    private Long id;
    private String name;
    private String email;
    private List<String> permissions;
    private List<String> roles;
}

