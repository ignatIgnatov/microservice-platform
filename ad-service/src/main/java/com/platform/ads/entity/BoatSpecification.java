package com.platform.ads.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("boat_specifications")
public class BoatSpecification {

    @Id
    private Long id;

    @Column("ad_id")
    private Long adId;

    @Column("boat_type")
    private String boatType;

    @Column("brand")
    private String brand;

    @Column("model")
    private String model;

    @Column("engine_type")
    private String engineType;

    @Column("engine_included")
    private Boolean engineIncluded;

    @Column("engine_brand_model")
    private String engineBrandModel;

    @Column("horsepower")
    private Integer horsepower;

    @Column("length")
    private BigDecimal length;

    @Column("width")
    private BigDecimal width;

    @Column("draft")
    private BigDecimal draft;

    @Column("max_people")
    private Integer maxPeople;

    @Column("year")
    private Integer year;

    @Column("in_warranty")
    private Boolean inWarranty;

    @Column("weight")
    private BigDecimal weight;

    @Column("fuel_capacity")
    private BigDecimal fuelCapacity;

    @Column("has_water_tank")
    private Boolean hasWaterTank;

    @Column("number_of_engines")
    private Integer numberOfEngines;

    @Column("has_auxiliary_engine")
    private Boolean hasAuxiliaryEngine;

    @Column("console_type")
    private String consoleType;

    @Column("fuel_type")
    private String fuelType;

    @Column("material")
    private String material;

    @Column("is_registered")
    private Boolean isRegistered;

    @Column("has_commercial_fishing_license")
    private Boolean hasCommercialFishingLicense;

    @Column("condition")
    private String condition;
}
