package com.platform.ads.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoatSearchRequest {

    @NotNull(message = "Category is required")
    private BoatCategory category;

    private String query;
    private String location;
    private PriceInfo.PriceType priceType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private ItemCondition condition;
    private AdType adType;
    private String sortBy; // PRICE_LOW_TO_HIGH, PRICE_HIGH_TO_LOW, NEWEST, OLDEST
    private Integer page;
    private Integer size;

    // Category-specific filters
    private BoatSearchFilters boatFilters;
    private JetSkiSearchFilters jetSkiFilters;
    private TrailerSearchFilters trailerFilters;
    private EngineSearchFilters engineFilters;
}
