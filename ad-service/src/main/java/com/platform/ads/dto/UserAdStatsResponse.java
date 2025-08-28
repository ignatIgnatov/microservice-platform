package com.platform.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdStatsResponse {

    private String userId;

    private String userEmail;

    private String userFullName;

    private Long totalAds;

    private Long activeAds;

    private Long inactiveAds;

    private BigDecimal totalValue; // Sum of all ad prices

    private BigDecimal averagePrice;
}
