package com.platform.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalsSpecificationResponse {
    private RentalsSpecificationDto.RentalType rentalType;
    private Boolean licenseRequired;
    private RentalsSpecificationDto.ManagementType managementType;
    private Integer numberOfPeople;
    private RentalsSpecificationDto.ServiceType serviceType;
    private String companyName;
    private String description;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String website;
    private BigDecimal maxPrice;
    private String priceSpecification;
}