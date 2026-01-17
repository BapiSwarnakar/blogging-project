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

    public static final String CATEGORY_READ = "CATEGORY_READ";
    public static final String CATEGORY_WRITE = "CATEGORY_WRITE";
    public static final String CATEGORY_DELETE = "CATEGORY_DELETE";
    public static final String CATEGORY_UPDATE = "CATEGORY_UPDATE";

    public static final String POST_READ = "POST_READ";
    public static final String POST_WRITE = "POST_WRITE";
    public static final String POST_DELETE = "POST_DELETE";
    public static final String POST_UPDATE = "POST_UPDATE";
}
