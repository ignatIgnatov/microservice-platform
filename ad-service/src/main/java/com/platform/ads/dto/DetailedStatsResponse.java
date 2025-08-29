package com.platform.ads.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class DetailedStatsResponse {
    private Long totalAds;
    private Long activeAds;
    private Long inactiveAds;
    private BigDecimal averagePrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Map<String, Long> adsByCategory;
    private Map<String, Long> adsByLocation;
    private Map<String, BigDecimal> averagePriceByCategory;
}