package com.platform.ads.dto;

import com.platform.ads.dto.enums.BoatCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoatMarketplaceStatsResponse {
    private Long totalAds;
    private Long activeAds;
    private Long inactiveAds;
    private BigDecimal averagePrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Map<BoatCategory, Long> adsByCategory;
    private Map<String, Long> adsByLocation;
    private Map<String, Long> popularBrands;
}
