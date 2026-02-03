package com.stech.payment.service;

import com.stech.payment.dto.request.PricingPlanDto;
import java.util.List;

public interface PricingPlanService {
    PricingPlanDto createPricingPlan(PricingPlanDto pricingPlanDto);
    PricingPlanDto updatePricingPlan(Long id, PricingPlanDto pricingPlanDto);
    void deletePricingPlan(Long id);
    PricingPlanDto getPricingPlanById(Long id);
    List<PricingPlanDto> getAllPricingPlans();
}
