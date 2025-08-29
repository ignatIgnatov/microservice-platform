package com.platform.ads.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("marine_electronics_specifications")
public class MarineElectronicsSpecification {
    @Id
    private Long id;
    private Long adId;
    private String electronicsType;
    private String brand;
    private String model;
    private Integer year;
    private Boolean inWarranty;
    private String condition;

    // Sonar specific fields
    private String workingFrequency;
    private String depthRange;
    private String screenSize;
    private Boolean probeIncluded;
    private String screenType;
    private Boolean gpsIntegrated;
    private Boolean bulgarianLanguage;

    // Probe specific fields
    private String power;
    private String frequency;
    private String material;
    private String rangeLength;
    private String mounting;

    // Trolling motor specific fields
    private Integer thrust;
    private String voltage;
    private String tubeLength;
    private String controlType;
    private String mountingType;
    private String motorType;
    private String waterResistance;
    private String weight;
}