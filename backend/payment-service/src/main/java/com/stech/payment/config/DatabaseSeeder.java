package com.stech.payment.config;

import com.stech.payment.entity.PricingPlanEntity;
import com.stech.payment.repository.PricingPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final PricingPlanRepository pricingPlanRepository;

    @Override
    public void run(String... args) throws Exception {
        seedDefaultPlans();
    }

    public void seedDefaultPlans() {
        if (pricingPlanRepository.count() == 0) {
            log.info("Seeding default pricing plans...");
            createPlan("Free Plan", 0.0, 10, 30, "Perfect for getting started with blogging.");
            createPlan("Basic Plan", 100.0, 110, 30, "Great for hobbyists and personal blogs.");
            createPlan("Business Plan", 500.0, 1000, 30, "Ideal for growing blogs and small businesses.");
            createPlan("Premium Plan", 1000.0, 2000, 30, "Advanced features for professional bloggers.");
            createPlan("Unlimited Plan", 5000.0, Integer.MAX_VALUE, 30, "Ultimate plan with no limits on creativity.");
            log.info("Default pricing plans seeded successfully.");
        }
    }

    private void createPlan(String name, Double price, Integer postLimit, Integer duration, String desc) {
        pricingPlanRepository.save(PricingPlanEntity.builder()
                .name(name)
                .price(price)
                .postLimit(postLimit)
                .durationDays(duration)
                .description(desc)
                .build());
    }
}
