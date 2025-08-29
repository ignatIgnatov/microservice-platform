package com.platform.ads.dto;

import com.platform.ads.dto.enums.ItemCondition;
import com.platform.ads.dto.enums.TrailerFeature;
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
public class TrailerSpecificationDto {

    @NotNull(message = "Trailer type is required")
    private TrailerType trailerType;

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand cannot exceed 100 characters")
    private String brand;

    @Size(max = 100, message = "Model cannot exceed 100 characters")
    private String model;

    @NotNull(message = "Axle count is required")
    private AxleCount axleCount;

    @NotNull(message = "Registration status is required")
    private Boolean isRegistered;

    @DecimalMin(value = "0.0", message = "Own weight cannot be negative")
    private BigDecimal ownWeight;

    @NotNull(message = "Load capacity is required")
    @DecimalMin(value = "0.1", message = "Load capacity must be positive")
    private BigDecimal loadCapacity;

    @NotNull(message = "Length is required")
    @DecimalMin(value = "0.1", message = "Length must be positive")
    private BigDecimal length;

    @NotNull(message = "Width is required")
    @DecimalMin(value = "0.1", message = "Width must be positive")
    private BigDecimal width;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2030, message = "Year cannot be in the future")
    private Integer year;

    private SuspensionType suspensionType;
    private KeelRollers keelRollers;

    @NotNull(message = "Warranty status is required")
    private Boolean inWarranty;

    @NotNull(message = "Condition is required")
    private ItemCondition condition;

    private List<TrailerFeature> features;

    public enum TrailerType {
        ALL("Всички"),
        JET_TRAILER("Колесар за джет"),
        BOAT_TRAILER("Колесар за лодка");

        private final String displayName;
        TrailerType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum AxleCount {
        ALL("Всички"),
        SINGLE("С една ос"),
        DOUBLE("С две оси"),
        TRIPLE("С три оси");

        private final String displayName;
        AxleCount(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum SuspensionType {
        DOUBLE_TORSION("х2 Торсионно"),
        TORSION("Торсионно"),
        LEAF_SPRING("Ресорно"),
        RIGID("Твърдо");

        private final String displayName;
        SuspensionType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum KeelRollers {
        ALL("Всички"),
        TWO_ROLLERS("С две ролки"),
        THREE_ROLLERS("С три ролки"),
        FOUR_ROLLERS("С четири ролки"),
        FIVE_ROLLERS("С пет ролки"),
        MULTIPLE_ROLLERS("С многобройни ролки");

        private final String displayName;
        KeelRollers(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
