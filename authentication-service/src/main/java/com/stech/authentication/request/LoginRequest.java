package com.stech.authentication.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    String username; 
    String password;
}

