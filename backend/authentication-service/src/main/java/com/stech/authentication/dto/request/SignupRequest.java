package com.stech.authentication.dto.request;

import java.time.LocalDate;
import java.util.Set;
import com.stech.authentication.enums.Gender;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupRequest {
    String firstName;
    String middleName;
    String lastName;
    String email;
    String password;
    Gender gender;
    String phone;
    LocalDate dateOfBirth;
    Set<String> roles;
    Set<String> directPermissions;
}
