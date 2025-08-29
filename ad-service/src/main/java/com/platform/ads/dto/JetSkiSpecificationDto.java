package com.platform.ads.dto;

import com.platform.ads.dto.enums.ItemCondition;
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
public class JetSkiSpecificationDto {

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand cannot exceed 100 characters")
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model cannot exceed 100 characters")
    private String model;

    @Size(max = 200, message = "Modification cannot exceed 200 characters")
    private String modification;

    @NotNull(message = "Registration status is required")
    private Boolean isRegistered;

    @NotNull(message = "Horsepower is required")
    @Min(value = 1, message = "Horsepower must be at least 1")
    @Max(value = 1000, message = "Horsepower cannot exceed 1000")
    private Integer horsepower;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2030, message = "Year cannot be in the future")
    private Integer year;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be positive")
    private BigDecimal weight;

    @NotNull(message = "Fuel capacity is required")
    @DecimalMin(value = "0.0", message = "Fuel capacity cannot be negative")
    private BigDecimal fuelCapacity;

    @NotNull(message = "Operating hours is required")
    @Min(value = 0, message = "Operating hours cannot be negative")
    @Max(value = 50000, message = "Operating hours seems too high")
    private Integer operatingHours;

    @NotNull(message = "Fuel type is required")
    private JetSkiFuelType fuelType;

    @NotNull(message = "Trailer included is required")
    private Boolean trailerIncluded;

    @NotNull(message = "Warranty status is required")
    private Boolean inWarranty;

    @NotNull(message = "Condition is required")
    private ItemCondition condition;

    public enum JetSkiFuelType {
        ALL("Всички"),
        PETROL("Бензин"),
        DIESEL("Дизел"),
        GAS("Газ");

        private final String displayName;
        JetSkiFuelType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
