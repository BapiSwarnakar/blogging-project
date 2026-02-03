package com.stech.payment.service.impl;

import com.stech.payment.entity.PricingPlanEntity;
import com.stech.payment.entity.UserSubscriptionEntity;
import com.stech.payment.enums.SubscriptionStatus;
import com.stech.payment.exception.ResourceNotFoundException;
import com.stech.payment.repository.PricingPlanRepository;
import com.stech.payment.repository.UserSubscriptionRepository;
import com.stech.payment.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final PricingPlanRepository pricingPlanRepository;

    @Override
    @Transactional
    public UserSubscriptionEntity getUserSubscription(Long userId) {
        Optional<UserSubscriptionEntity> activeSub = userSubscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
        
        if (activeSub.isPresent()) {
            return activeSub.get();
        }

        // If no active sub, check if user ever had a sub
        Optional<UserSubscriptionEntity> lastSub = userSubscriptionRepository.findTopByUserIdOrderByEndDateDesc(userId);
        
        // If no sub at all or last sub expired, create/reuse Free Plan
        PricingPlanEntity freePlan = pricingPlanRepository.findByName("Free Plan")
                .orElseThrow(() -> new ResourceNotFoundException("Free Plan not found in database"));

        UserSubscriptionEntity newSub = UserSubscriptionEntity.builder()
                .userId(userId)
                .plan(freePlan)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusYears(100)) // Free plan effectively never expires
                .status(SubscriptionStatus.ACTIVE)
                .build();

        return userSubscriptionRepository.save(newSub);
    }

    @Override
    @Transactional
    public UserSubscriptionEntity upgradeSubscription(Long userId, Long planId) {
        PricingPlanEntity newPlan = pricingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Target plan not found"));

        // Deactivate current active plan
        userSubscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .ifPresent(sub -> {
                    sub.setStatus(SubscriptionStatus.UPGRADED);
                    userSubscriptionRepository.save(sub);
                });

        UserSubscriptionEntity newSub = UserSubscriptionEntity.builder()
                .userId(userId)
                .plan(newPlan)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(newPlan.getDurationDays()))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        return userSubscriptionRepository.save(newSub);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *") // Run every day at midnight
    @Transactional
    public void checkAndExpireSubscriptions() {
        // Simple logic: find all active subscriptions where endDate < now and status is ACTIVE
        // Note: For production, use a more optimized query
        userSubscriptionRepository.findAll().stream()
                .filter(sub -> SubscriptionStatus.ACTIVE.equals(sub.getStatus()) && sub.getEndDate().isBefore(LocalDateTime.now()))
                .forEach(sub -> {
                    sub.setStatus(SubscriptionStatus.EXPIRED);
                    userSubscriptionRepository.save(sub);
                    // Optionally trigger an event or reset to free plan here
                });
    }
}
