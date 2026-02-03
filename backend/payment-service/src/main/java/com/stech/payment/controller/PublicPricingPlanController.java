package com.stech.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stech.common.library.GlobalApiResponse;
import com.stech.payment.dto.request.PricingPlanDto;
import com.stech.payment.service.PricingPlanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payment/public/pricing-plans")
@RequiredArgsConstructor
public class PublicPricingPlanController {

    private final PricingPlanService pricingPlanService;

    @GetMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<PricingPlanDto>> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(GlobalApiResponse.success(pricingPlanService.getPricingPlanById(id), "Pricing plan fetched successfully"));
    }

    @GetMapping
    public ResponseEntity<GlobalApiResponse.ApiResult<List<PricingPlanDto>>> getAllPlans() {
        return ResponseEntity.ok(GlobalApiResponse.success(pricingPlanService.getAllPricingPlans(), "Pricing plans fetched successfully"));
    }
}

