package com.platform.ads.dto;

import com.platform.ads.dto.enums.ItemCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterSportsSpecificationResponse {
    private WaterSportsSpecificationDto.WaterSportsType waterSportsType;
    private String brand;
    private ItemCondition condition;
}