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
@Table("engine_specifications")
public class EngineSpecification {

    @Id
    private Long id;

    @Column("ad_id")
    private Long adId;

    @Column("engine_type")
    private String engineType;

    @Column("brand")
    private String brand;

    @Column("modification")
    private String modification;

    @Column("stroke_type")
    private String strokeType;

    @Column("in_warranty")
    private Boolean inWarranty;

    @Column("horsepower")
    private Integer horsepower;

    @Column("operating_hours")
    private Integer operatingHours;

    @Column("cylinders")
    private Integer cylinders;

    @Column("displacement_cc")
    private Integer displacementCc;

    @Column("rpm")
    private Integer rpm;

    @Column("weight")
    private BigDecimal weight;

    @Column("year")
    private Integer year;

    @Column("fuel_capacity")
    private BigDecimal fuelCapacity;

    @Column("ignition_type")
    private String ignitionType;

    @Column("control_type")
    private String controlType;

    @Column("shaft_length")
    private String shaftLength;

    @Column("fuel_type")
    private String fuelType;

    @Column("engine_system_type")
    private String engineSystemType;

    @Column("condition")
    private String condition;

    @Column("color")
    private String color;
}
