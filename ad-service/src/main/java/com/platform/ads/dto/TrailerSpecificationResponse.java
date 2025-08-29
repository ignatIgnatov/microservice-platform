package com.platform.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrailerSpecificationResponse {
    private TrailerSpecificationDto.TrailerType trailerType;
    private String brand;
    private String model;
    private TrailerSpecificationDto.AxleCount axleCount;
    private Boolean isRegistered;
    private BigDecimal ownWeight;
    private BigDecimal loadCapacity;
    private BigDecimal length;
    private BigDecimal width;
    private Integer year;
    private TrailerSpecificationDto.SuspensionType suspensionType;
    private TrailerSpecificationDto.KeelRollers keelRollers;
    private Boolean inWarranty;
    private ItemCondition condition;
    private List<TrailerFeature> features;
}
