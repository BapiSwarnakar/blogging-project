package com.stech.authentication.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    PENDING(1),
    APPROVED(2),
    REJECTED(3);

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }
}
