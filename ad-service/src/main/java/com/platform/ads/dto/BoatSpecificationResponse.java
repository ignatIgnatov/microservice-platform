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
public class BoatSpecificationResponse {
    private BoatSpecificationDto.BoatType type;
    private String brand;
    private String model;
    private BoatSpecificationDto.EngineType engineType;
    private Boolean engineIncluded;
    private String engineBrandModel;
    private Integer horsepower;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal draft;
    private Integer maxPeople;
    private Integer year;
    private Boolean inWarranty;
    private BigDecimal weight;
    private BigDecimal fuelCapacity;
    private Boolean hasWaterTank;
    private Integer numberOfEngines;
    private Boolean hasAuxiliaryEngine;
    private BoatSpecificationDto.ConsoleType consoleType;
    private BoatSpecificationDto.FuelType fuelType;
    private BoatSpecificationDto.MaterialType material;
    private Boolean isRegistered;
    private Boolean hasCommercialFishingLicense;
    private ItemCondition condition;
    private List<InteriorFeature> interiorFeatures;
    private List<ExteriorFeature> exteriorFeatures;
    private List<Equipment> equipment;
}
