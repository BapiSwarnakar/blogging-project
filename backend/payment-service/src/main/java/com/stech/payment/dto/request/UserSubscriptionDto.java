package com.stech.payment.dto.request;

import com.stech.payment.enums.SubscriptionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscriptionDto {
    private Long id;
    private Long userId;
    private PricingPlanDto plan;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SubscriptionStatus status;
    private Integer remainingPosts;
}
