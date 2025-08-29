package com.platform.ads.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class CategoryStatsResponse {
    private Map<String, Long> totalByCategory;
    private Map<String, Long> activeByCategory;
    private Map<String, BigDecimal> averagePriceByCategory;
}
