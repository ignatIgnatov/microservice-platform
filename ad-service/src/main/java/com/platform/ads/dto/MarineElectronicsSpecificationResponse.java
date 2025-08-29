package com.platform.ads.dto;

import com.platform.ads.dto.enums.ItemCondition;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarineElectronicsSpecificationResponse {
    private MarineElectronicsSpecificationDto.ElectronicsType electronicsType;
    private String brand;
    private String model;
    private Integer year;
    private Boolean inWarranty;
    private ItemCondition condition;

    // Sonar fields
    private MarineElectronicsSpecificationDto.WorkingFrequency workingFrequency;
    private MarineElectronicsSpecificationDto.DepthRange depthRange;
    private MarineElectronicsSpecificationDto.ScreenSize screenSize;
    private Boolean probeIncluded;
    private MarineElectronicsSpecificationDto.ScreenType screenType;
    private Boolean gpsIntegrated;
    private Boolean bulgarianLanguage;

    // Probe fields
    private MarineElectronicsSpecificationDto.Power power;
    private MarineElectronicsSpecificationDto.Frequency frequency;
    private String material;
    private MarineElectronicsSpecificationDto.RangeLength rangeLength;
    private MarineElectronicsSpecificationDto.Mounting mounting;

    // Trolling motor fields
    private Integer thrust;
    private MarineElectronicsSpecificationDto.Voltage voltage;
    private MarineElectronicsSpecificationDto.TubeLength tubeLength;
    private MarineElectronicsSpecificationDto.TrollingControlType controlType;
    private MarineElectronicsSpecificationDto.MountingType mountingType;
    private MarineElectronicsSpecificationDto.MotorType motorType;
    private MarineElectronicsSpecificationDto.WaterResistance waterResistance;
    private MarineElectronicsSpecificationDto.Weight weight;
}
