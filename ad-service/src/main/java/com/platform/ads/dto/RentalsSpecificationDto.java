package com.platform.ads.dto;

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
public class RentalsSpecificationDto {

    @NotNull(message = "Rental type is required")
    private RentalType rentalType;

    @NotNull(message = "License requirement is required")
    private Boolean licenseRequired;

    @NotNull(message = "Management type is required")
    private ManagementType managementType;

    @NotNull(message = "Number of people is required")
    private Integer numberOfPeople;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name cannot exceed 200 characters")
    private String companyName;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    private String contactEmail;
    private String address;
    private String website;

    @NotNull(message = "Maximum price is required")
    private BigDecimal maxPrice;

    @NotBlank(message = "Price specification is required")
    private String priceSpecification;

    public enum RentalType {
        ALL("Всички"),
        MOTOR_BOAT("Моторна Лодка"),
        MOTOR_YACHT("Моторна Яхта"),
        CATAMARAN("Катамаран"),
        SAILING_BOAT_YACHT("Ветроходна лодка или яхта"),
        JET("Джет"),
        WATER_SPORTS("Морски спортове");

        private final String displayName;
        RentalType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum ManagementType {
        ALL("Всички"),
        SELF_DRIVE("Управлявай сам"),
        WITH_CAPTAIN("С капитан");

        private final String displayName;
        ManagementType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum ServiceType {
        ALL("Всички"),
        SHARED_TRIP("Споделено пътуване"),
        PRIVATE_RENTAL("Частно наемане");

        private final String displayName;
        ServiceType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}