package com.platform.ads.dto;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngineSpecificationDto {

    @NotNull(message = "Engine type is required")
    private EngineTypeMain engineType;

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand cannot exceed 100 characters")
    private String brand;

    @Size(max = 200, message = "Modification cannot exceed 200 characters")
    private String modification;

    @NotNull(message = "Stroke type is required")
    private StrokeType strokeType;

    @NotNull(message = "Warranty status is required")
    private Boolean inWarranty;

    @NotNull(message = "Horsepower is required")
    @Min(value = 1, message = "Horsepower must be at least 1")
    @Max(value = 10000, message = "Horsepower cannot exceed 10000")
    private Integer horsepower;

    @NotNull(message = "Operating hours is required")
    @Min(value = 0, message = "Operating hours cannot be negative")
    private Integer operatingHours;

    @Min(value = 1, message = "Cylinders must be at least 1")
    @Max(value = 50, message = "Cylinders cannot exceed 50")
    private Integer cylinders;

    @Min(value = 1, message = "Displacement must be positive")
    private Integer displacementCc;

    @Min(value = 1, message = "RPM must be positive")
    private Integer rpm;

    @DecimalMin(value = "0.1", message = "Weight must be positive")
    private BigDecimal weight;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2030, message = "Year cannot be in the future")
    private Integer year;

    @NotNull(message = "Fuel capacity is required")
    @DecimalMin(value = "0.0", message = "Fuel capacity cannot be negative")
    private BigDecimal fuelCapacity;

    @NotNull(message = "Ignition type is required")
    private IgnitionType ignitionType;

    @NotNull(message = "Control type is required")
    private ControlType controlType;

    @NotNull(message = "Shaft length is required")
    private ShaftLength shaftLength;

    @NotNull(message = "Fuel type is required")
    private EngineFuelType fuelType;

    @NotNull(message = "Engine system type is required")
    private EngineSystemType engineSystemType;

    @NotNull(message = "Condition is required")
    private ItemCondition condition;

    @NotNull(message = "Color is required")
    private EngineColor color;

    public enum EngineTypeMain {
        ALL("Всички"),
        OUTBOARD("Извънбордов двигател"),
        INBOARD("Бордов / Вътрешен двигател");

        private final String displayName;
        EngineTypeMain(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum StrokeType {
        ALL("Всички"),
        TWO_STROKE("Двутактов"),
        FOUR_STROKE("Четиритактов");

        private final String displayName;
        StrokeType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum IgnitionType {
        ALL("Всички"),
        MANUAL("Ръчно"),
        ELECTRIC("Електрическо");

        private final String displayName;
        IgnitionType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum ControlType {
        ALL("Всички"),
        HANDLE("С ръчка"),
        HYDRAULIC("Хидравлично");

        private final String displayName;
        ControlType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum ShaftLength {
        ALL("Всички"),
        S("S"),
        M("M"),
        L("L"),
        XL("XL");

        private final String displayName;
        ShaftLength(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum EngineFuelType {
        ALL("Всички"),
        PETROL("Бензин"),
        DIESEL("Дизел"),
        LPG("Пропан-бутан");

        private final String displayName;
        EngineFuelType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum EngineSystemType {
        ALL("Всички"),
        CARBURETOR("Карбураторен"),
        EFI("EFI (инжекция)");

        private final String displayName;
        EngineSystemType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum EngineColor {
        BLACK("Черен"),
        GREY("Сив"),
        YELLOW("Жълт"),
        WHITE("Бял"),
        BLUE("Син"),
        ORANGE("Оранжев"),
        RED("Червен"),
        GREEN("Зелен"),
        PURPLE("Лилав");

        private final String displayName;
        EngineColor(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
