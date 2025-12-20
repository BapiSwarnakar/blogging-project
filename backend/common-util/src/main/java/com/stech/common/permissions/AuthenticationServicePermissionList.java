package com.stech.common.permissions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AuthenticationServicePermissionList {

    AuthenticationServicePermissionList() {
        log.info("AuthenticationServicePermissionList initialized");
    }
    
    public static final String PERMISSION_READ = "PERMISSION_READ";
    public static final String PERMISSION_WRITE = "PERMISSION_WRITE";
    public static final String PERMISSION_DELETE = "PERMISSION_DELETE";
    public static final String PERMISSION_UPDATE = "PERMISSION_UPDATE";
}
