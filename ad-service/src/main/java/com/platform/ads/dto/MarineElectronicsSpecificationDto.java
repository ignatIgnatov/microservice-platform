package com.platform.ads.dto;

import com.platform.ads.dto.enums.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MarineElectronicsSpecificationDto {
    private ElectronicsType electronicsType;
    private String brand;
    private String model;
    private Integer year;
    private Boolean inWarranty;
    private ItemCondition condition;

    // Sonar specific fields
    private WorkingFrequency workingFrequency;
    private DepthRange depthRange;
    private ScreenSize screenSize;
    private Boolean probeIncluded;
    private ScreenType screenType;
    private Boolean gpsIntegrated;
    private Boolean bulgarianLanguage;
    private List<String> connectivity;
    private List<String> ports;
    private List<String> includedInBox;

    // Probe specific fields
    private Power power;
    private Frequency frequency;
    private String material;
    private RangeLength rangeLength;
    private Mounting mounting;
    private List<String> sensorTypes;

    // Trolling motor specific fields
    private Integer thrust;
    private Voltage voltage;
    private TubeLength tubeLength;
    private TrollingControlType controlType;
    private MountingType mountingType;
    private MotorType motorType;
    private WaterResistance waterResistance;
    private Weight weight;
    private List<String> controlFunctions;

    // Nested enums for Marine Electronics
    public enum ElectronicsType {
        SONAR, PROBE, TROLLING_MOTOR
    }

    public enum WorkingFrequency {
        LOW, MEDIUM, HIGH, LOW_CHIRP, MEDIUM_CHIRP, HIGH_CHIRP
    }

    public enum DepthRange {
        UP_TO_50M, FROM_50_TO_100M, OVER_100M
    }

    public enum ScreenSize {
        FOUR_INCH, FIVE_INCH, SEVEN_INCH, NINE_INCH, TEN_INCH, TWELVE_INCH, SIXTEEN_INCH
    }

    public enum ScreenType {
        WITH_BUTTONS, TOUCH_SCREEN, TOUCH_SCREEN_WITH_BUTTONS
    }

    public enum Power {
        W_300, W_600, KW_1, KW_2, KW_3
    }

    public enum Frequency {
        LOW, MEDIUM, MEDIUM_WIDE, HIGH, HIGH_WIDE
    }

    public enum RangeLength {
        UP_TO_60M, FROM_60_TO_100M, OVER_100M
    }

    public enum Mounting {
        ON_TRANSOM, THROUGH_HULL, ON_TROLLING_MOTOR
    }

    public enum Voltage {
        V_12, V_24, V_36
    }

    public enum TubeLength {
        THIRTY_INCH, THIRTY_SIX_INCH, FORTY_EIGHT_INCH, FIFTY_FOUR_INCH,
        SIXTY_INCH, SIXTY_THREE_INCH, SEVENTY_TWO_INCH, SEVENTY_FIVE_INCH, NINETY_INCH
    }

    public enum TrollingControlType {
        MANUAL_CONTROL, FOOT_CONTROL, REMOTE_CONTROL, SONAR_CONTROL
    }

    public enum MountingType {
        BOW_MOUNT, TRANSOM_MOUNT
    }

    public enum MotorType {
        BRUSHLESS, BRUSHED
    }

    public enum WaterResistance {
        SALT_WATER_RESISTANT, FRESH_WATER_RESISTANT
    }

    public enum Weight {
        LIGHT_PORTABLE, STANDARD_HEAVY
    }
}