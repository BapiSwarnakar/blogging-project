package com.stech.authentication.dto.response;

import java.util.Set;
import com.stech.authentication.enums.Gender;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    Long id; 
    String name; 
    String email; 
    Gender gender;
    Set<String> permissions;
}
