package com.stech.payment.repository;

import com.stech.payment.entity.PricingPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingPlanRepository extends JpaRepository<PricingPlanEntity, Long> {
    Optional<PricingPlanEntity> findByName(String name);
}
