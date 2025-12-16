package com.stech.common.library;

public class CommunicationServiceUtil {

    private static final String API_AUTHENTICATION_SERVICE_MICROSERVICE = "http://AUTHENTICATION-SERVICE";
    private static final String API_USER_MANAGEMENT_MICROSERVICE = "http://USER-SERVICE";
    private static final String API_PAYMENT_SYNC_SERVICE_MICROSERVICE = "http://PAYMENT-SYNC-SERVICE";

    private CommunicationServiceUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getAuthenticationServiceMicroserviceUrl() {
        return API_AUTHENTICATION_SERVICE_MICROSERVICE+"/api/v1/auth";
    }

    public static String getUserManagementMicroserviceUrl() {
        return API_USER_MANAGEMENT_MICROSERVICE+"/api/v1/user";
    }

    public static String getPaymentSyncServiceMicroserviceUrl() {
        return API_PAYMENT_SYNC_SERVICE_MICROSERVICE+"/api/v1/payment";
    }
}
