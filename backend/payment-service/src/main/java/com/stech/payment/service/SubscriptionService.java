package com.stech.payment.service;

import com.stech.payment.entity.UserSubscriptionEntity;

public interface SubscriptionService {
    UserSubscriptionEntity getUserSubscription(Long userId);
    UserSubscriptionEntity upgradeSubscription(Long userId, Long planId);
    void checkAndExpireSubscriptions();
}
