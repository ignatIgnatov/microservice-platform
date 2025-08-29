package com.platform.ads.dto;

import com.platform.ads.dto.enums.ItemCondition;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FishingSpecificationResponse {
    private FishingSpecificationDto.FishingType fishingType;
    private String brand;
    private FishingSpecificationDto.FishingTechnique fishingTechnique;
    private FishingSpecificationDto.TargetFish targetFish;
    private ItemCondition condition;
}
