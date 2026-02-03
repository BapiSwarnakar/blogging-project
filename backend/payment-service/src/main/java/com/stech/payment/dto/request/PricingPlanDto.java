package com.stech.payment.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingPlanDto {
    private Long id;
    private String name;
    private Double price;
    private Integer postLimit;
    private Integer durationDays;
    private String description;
}
