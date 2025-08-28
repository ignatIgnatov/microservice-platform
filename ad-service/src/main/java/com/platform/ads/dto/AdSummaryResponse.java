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
public class AdSummaryResponse {

    private Long totalAds;

    private Long activeAds;

    private Long inactiveAds;

    private BigDecimal averagePrice;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;
}
