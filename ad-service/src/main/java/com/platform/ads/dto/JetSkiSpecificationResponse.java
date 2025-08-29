package com.platform.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JetSkiSpecificationResponse {
    private String brand;
    private String model;
    private String modification;
    private Boolean isRegistered;
    private Integer horsepower;
    private Integer year;
    private BigDecimal weight;
    private BigDecimal fuelCapacity;
    private Integer operatingHours;
    private JetSkiSpecificationDto.JetSkiFuelType fuelType;
    private Boolean trailerIncluded;
    private Boolean inWarranty;
    private ItemCondition condition;
}
