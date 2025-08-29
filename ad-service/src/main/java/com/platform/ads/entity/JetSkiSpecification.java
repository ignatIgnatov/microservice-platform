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
@Table("jetski_specifications")
public class JetSkiSpecification {

    @Id
    private Long id;

    @Column("ad_id")
    private Long adId;

    @Column("brand")
    private String brand;

    @Column("model")
    private String model;

    @Column("modification")
    private String modification;

    @Column("is_registered")
    private Boolean isRegistered;

    @Column("horsepower")
    private Integer horsepower;

    @Column("year")
    private Integer year;

    @Column("weight")
    private BigDecimal weight;

    @Column("fuel_capacity")
    private BigDecimal fuelCapacity;

    @Column("operating_hours")
    private Integer operatingHours;

    @Column("fuel_type")
    private String fuelType;

    @Column("trailer_included")
    private Boolean trailerIncluded;

    @Column("in_warranty")
    private Boolean inWarranty;

    @Column("condition")
    private String condition;
}
