package com.platform.ads.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class BoatSpecificationDto {

    @NotNull(message = "Boat type is required")
    private BoatType type;

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand cannot exceed 100 characters")
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model cannot exceed 100 characters")
    private String model;

    @NotNull(message = "Engine type is required")
    private EngineType engineType;

    @NotNull(message = "Engine included is required")
    private Boolean engineIncluded;

    @Size(max = 200, message = "Engine brand/model cannot exceed 200 characters")
    private String engineBrandModel;

    @NotNull(message = "Horsepower is required")
    @Min(value = 1, message = "Horsepower must be at least 1")
    @Max(value = 10000, message = "Horsepower cannot exceed 10000")
    private Integer horsepower;

    @NotNull(message = "Length is required")
    @DecimalMin(value = "0.1", message = "Length must be positive")
    @DecimalMax(value = "500.0", message = "Length cannot exceed 500m")
    private BigDecimal length;

    @NotNull(message = "Width is required")
    @DecimalMin(value = "0.1", message = "Width must be positive")
    @DecimalMax(value = "100.0", message = "Width cannot exceed 100m")
    private BigDecimal width;

    @DecimalMin(value = "0.0", message = "Draft cannot be negative")
    @DecimalMax(value = "50.0", message = "Draft cannot exceed 50m")
    private BigDecimal draft;

    @NotNull(message = "Max people is required")
    @Min(value = 1, message = "Max people must be at least 1")
    @Max(value = 1000, message = "Max people cannot exceed 1000")
    private Integer maxPeople;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2030, message = "Year cannot be in the future")
    private Integer year;

    @NotNull(message = "Warranty status is required")
    private Boolean inWarranty;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be positive")
    private BigDecimal weight;

    @NotNull(message = "Fuel capacity is required")
    @DecimalMin(value = "0.0", message = "Fuel capacity cannot be negative")
    private BigDecimal fuelCapacity;

    @NotNull(message = "Water tank status is required")
    private Boolean hasWaterTank;

    @NotNull(message = "Number of engines is required")
    @Min(value = 0, message = "Number of engines cannot be negative")
    @Max(value = 10, message = "Number of engines cannot exceed 10")
    private Integer numberOfEngines;

    @NotNull(message = "Auxiliary engine status is required")
    private Boolean hasAuxiliaryEngine;

    private ConsoleType consoleType;
    private FuelType fuelType;
    private MaterialType material;

    @NotNull(message = "Registration status is required")
    private Boolean isRegistered;

    private Boolean hasCommercialFishingLicense;

    @NotNull(message = "Condition is required")
    private ItemCondition condition;

    private List<InteriorFeature> interiorFeatures;
    private List<ExteriorFeature> exteriorFeatures;
    private List<Equipment> equipment;

    public enum BoatType {
        ALL("Всички"),
        MOTOR_BOAT("Моторна Лодка / Яхта"),
        SAILING_BOAT("Ветроходна Лодка / Яхта"),
        KAYAK_CANOE("Каяк / Кану");

        private final String displayName;
        BoatType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum EngineType {
        ALL("Всички"),
        OUTBOARD("Извънбордов"),
        INBOARD("Бордов"),
        NONE("Без");

        private final String displayName;
        EngineType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum ConsoleType {
        ALL("Всички"),
        NONE("Няма"),
        CENTRAL("Централна конзола"),
        SIDE("Странична конзола"),
        CABIN("Конзола в кабината"),
        FLYBRIDGE("Конзола на горна палуба(Flybridge)");

        private final String displayName;
        ConsoleType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum FuelType {
        ALL("Всички"),
        PETROL("Бензин"),
        DIESEL("Дизел"),
        LPG("Пропан бутан"),
        HYDROGEN("Водород");

        private final String displayName;
        FuelType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum MaterialType {
        ALL("Всички"),
        FIBERGLASS("Стъклопласт"),
        WOOD("Дървена"),
        ALUMINUM("Алуминий"),
        PVC("PVC"),
        HYPALON("Хипалон"),
        RUBBER("Гума");

        private final String displayName;
        MaterialType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
