package com.stech.common.permissions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PaymentServicePermissionList {

    private PaymentServicePermissionList() {
        log.info("PaymentServicePermissionList initialized");
    }

    public static final String PAYMENT_READ = "PAYMENT_READ";
    public static final String PAYMENT_WRITE = "PAYMENT_WRITE";
    public static final String PAYMENT_DELETE = "PAYMENT_DELETE";
    public static final String PAYMENT_UPDATE = "PAYMENT_UPDATE";
}
