package com.platform.ads.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAdsStatisticsResponse {
    private String userId;
    private Long totalAds;
    private Long activeAds;
    private Long inactiveAds;
    private Long totalViews;
    private Integer mostViewedAdViews;
    private Double averageViewsPerAd;
}
