package com.platform.ads.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class UserStatisticsResponse {
    private Map<String, Long> topSellersByAdCount;
    private Map<String, Long> adCountsByDate;
    private Map<String, Long> approvalStatusBreakdown;
    private Long totalUniqueUsers;
    private Long activeUsersLast30Days;
    private Double averageAdsPerUser;
}