package com.stech.payment.service.impl;

import com.stech.payment.dto.request.PricingPlanDto;
import com.stech.payment.entity.PricingPlanEntity;
import com.stech.payment.exception.ResourceNotFoundException;
import com.stech.payment.repository.PricingPlanRepository;
import com.stech.payment.service.PricingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingPlanServiceImpl implements PricingPlanService {

    private final PricingPlanRepository pricingPlanRepository;

    @Override
    public PricingPlanDto createPricingPlan(PricingPlanDto pricingPlanDto) {
        PricingPlanEntity entity = mapToEntity(pricingPlanDto);
        PricingPlanEntity saved = pricingPlanRepository.save(entity);
        return mapToDto(saved);
    }

    @Override
    public PricingPlanDto updatePricingPlan(Long id, PricingPlanDto pricingPlanDto) {
        PricingPlanEntity entity = pricingPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing plan not found with id: " + id));
        
        entity.setName(pricingPlanDto.getName());
        entity.setPrice(pricingPlanDto.getPrice());
        entity.setPostLimit(pricingPlanDto.getPostLimit());
        entity.setDurationDays(pricingPlanDto.getDurationDays());
        entity.setDescription(pricingPlanDto.getDescription());
        
        PricingPlanEntity updated = pricingPlanRepository.save(entity);
        return mapToDto(updated);
    }

    @Override
    public void deletePricingPlan(Long id) {
        pricingPlanRepository.deleteById(id);
    }

    @Override
    public PricingPlanDto getPricingPlanById(Long id) {
        PricingPlanEntity entity = pricingPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing plan not found with id: " + id));
        return mapToDto(entity);
    }

    @Override
    public List<PricingPlanDto> getAllPricingPlans() {
        return pricingPlanRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }



    private PricingPlanDto mapToDto(PricingPlanEntity entity) {
        return PricingPlanDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .postLimit(entity.getPostLimit())
                .durationDays(entity.getDurationDays())
                .description(entity.getDescription())
                .build();
    }

    private PricingPlanEntity mapToEntity(PricingPlanDto dto) {
        return PricingPlanEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .postLimit(dto.getPostLimit())
                .durationDays(dto.getDurationDays())
                .description(dto.getDescription())
                .build();
    }
}
