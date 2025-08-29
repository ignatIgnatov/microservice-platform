package com.platform.ads.dto;

import com.platform.ads.dto.enums.Equipment;
import com.platform.ads.dto.enums.ExteriorFeature;
import com.platform.ads.dto.enums.InteriorFeature;
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
public class BoatSearchFilters {
    private BoatSpecificationDto.BoatType boatType;
    private String brand;
    private BoatSpecificationDto.EngineType engineType;
    private Integer minHorsepower;
    private Integer maxHorsepower;
    private BigDecimal minLength;
    private BigDecimal maxLength;
    private BigDecimal minWidth;
    private BigDecimal maxWidth;
    private Integer minYear;
    private Integer maxYear;
    private BoatSpecificationDto.MaterialType material;
    private BoatSpecificationDto.FuelType fuelType;
    private Boolean isRegistered;
    private Boolean inWarranty;
    private Boolean engineIncluded;
    private BoatSpecificationDto.ConsoleType consoleType;
    private List<InteriorFeature> interiorFeatures;
    private List<ExteriorFeature> exteriorFeatures;
    private List<Equipment> equipment;
}
