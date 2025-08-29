package com.platform.ads.dto;

import com.platform.ads.dto.enums.ItemCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngineSpecificationResponse {
    private EngineSpecificationDto.EngineTypeMain engineType;
    private String brand;
    private String modification;
    private EngineSpecificationDto.StrokeType strokeType;
    private Boolean inWarranty;
    private Integer horsepower;
    private Integer operatingHours;
    private Integer cylinders;
    private Integer displacementCc;
    private Integer rpm;
    private BigDecimal weight;
    private Integer year;
    private BigDecimal fuelCapacity;
    private EngineSpecificationDto.IgnitionType ignitionType;
    private EngineSpecificationDto.ControlType controlType;
    private EngineSpecificationDto.ShaftLength shaftLength;
    private EngineSpecificationDto.EngineFuelType fuelType;
    private EngineSpecificationDto.EngineSystemType engineSystemType;
    private ItemCondition condition;
    private EngineSpecificationDto.EngineColor color;
}
