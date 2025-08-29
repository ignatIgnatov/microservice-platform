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
public class TrailerSearchFilters {
    private TrailerSpecificationDto.TrailerType trailerType;
    private String brand;
    private TrailerSpecificationDto.AxleCount axleCount;
    private BigDecimal minLoadCapacity;
    private BigDecimal maxLoadCapacity;
    private Integer minYear;
    private Integer maxYear;
    private Boolean isRegistered;
    private Boolean inWarranty;
    private TrailerSpecificationDto.KeelRollers keelRollers;
    private List<TrailerFeature> features;
}
