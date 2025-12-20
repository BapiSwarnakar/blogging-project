package com.stech.common.permissions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class UserManagementServicePermissionList {

    private UserManagementServicePermissionList() {
        log.info("UserManagementServicePermissionList initialized");
    }

    public static final String USER_READ = "USER_READ";
    public static final String USER_WRITE = "USER_WRITE";
    public static final String USER_DELETE = "USER_DELETE";
    public static final String USER_UPDATE = "USER_UPDATE";
}
