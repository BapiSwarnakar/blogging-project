package com.stech.payment.controller;

import com.stech.payment.dto.request.PricingPlanDto;
import com.stech.payment.service.PricingPlanService;
import com.stech.common.library.GlobalApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment/pricing-plans")
@RequiredArgsConstructor
public class PricingPlanController {

    private final PricingPlanService pricingPlanService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalApiResponse.ApiResult<PricingPlanDto>> createPlan(@RequestBody PricingPlanDto pricingPlanDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalApiResponse.success(pricingPlanService.createPricingPlan(pricingPlanDto), "Pricing plan created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalApiResponse.ApiResult<PricingPlanDto>> updatePlan(@PathVariable Long id, @RequestBody PricingPlanDto pricingPlanDto) {
        return ResponseEntity.ok(GlobalApiResponse.success(pricingPlanService.updatePricingPlan(id, pricingPlanDto), "Pricing plan updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalApiResponse.ApiResult<Void>> deletePlan(@PathVariable Long id) {
        pricingPlanService.deletePricingPlan(id);
        return ResponseEntity.ok(GlobalApiResponse.success(null, "Pricing plan deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<PricingPlanDto>> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(GlobalApiResponse.success(pricingPlanService.getPricingPlanById(id), "Pricing plan fetched successfully"));
    }

    @GetMapping
    public ResponseEntity<GlobalApiResponse.ApiResult<List<PricingPlanDto>>> getAllPlans() {
        return ResponseEntity.ok(GlobalApiResponse.success(pricingPlanService.getAllPricingPlans(), "Pricing plans fetched successfully"));
    }
}

