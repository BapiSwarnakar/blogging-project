package com.stech.payment.repository;

import com.stech.payment.entity.UserSubscriptionEntity;
import com.stech.payment.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscriptionEntity, Long> {
    Optional<UserSubscriptionEntity> findByUserIdAndStatus(Long userId, SubscriptionStatus status);
    Optional<UserSubscriptionEntity> findTopByUserIdOrderByEndDateDesc(Long userId);
}
