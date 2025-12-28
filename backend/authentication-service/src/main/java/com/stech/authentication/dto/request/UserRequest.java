package com.stech.authentication.dto.request;

import java.time.LocalDate;
import java.util.Set;
import com.stech.authentication.enums.Gender;
import com.stech.authentication.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private Gender gender;
    private String phone;
    private LocalDate dateOfBirth;
    private Set<String> roles;
    private Set<String> directPermissions;
    private boolean isActive;
    private UserStatus userStatus;
}
