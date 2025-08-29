package com.platform.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngineSearchFilters {
    private EngineSpecificationDto.EngineTypeMain engineType;
    private String brand;
    private EngineSpecificationDto.StrokeType strokeType;
    private Integer minHorsepower;
    private Integer maxHorsepower;
    private Integer minYear;
    private Integer maxYear;
    private EngineSpecificationDto.EngineFuelType fuelType;
    private EngineSpecificationDto.IgnitionType ignitionType;
    private EngineSpecificationDto.ControlType controlType;
    private EngineSpecificationDto.ShaftLength shaftLength;
    private EngineSpecificationDto.EngineColor color;
    private Boolean inWarranty;
}
