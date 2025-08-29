package com.platform.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JetSkiSearchFilters {
    private String brand;
    private String model;
    private Integer minHorsepower;
    private Integer maxHorsepower;
    private Integer minYear;
    private Integer maxYear;
    private JetSkiSpecificationDto.JetSkiFuelType fuelType;
    private Boolean trailerIncluded;
    private Boolean inWarranty;
    private Boolean isRegistered;
}
