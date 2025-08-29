package com.platform.ads.dto;

import com.platform.ads.dto.enums.ItemCondition;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartsSpecificationResponse {
    private PartsSpecificationDto.PartType partType;
    private ItemCondition condition;
}
