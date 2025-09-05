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
public class MarineAccessoriesSpecificationResponse {
    private MarineAccessoriesSpecificationDto.AccessoryType accessoryType;
    private String brand;
    private ItemCondition condition;
}