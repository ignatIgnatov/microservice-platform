package com.platform.ads.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("rentals_specifications")
public class RentalsSpecification {
    @Id
    private Long id;
    private Long adId;
    private String rentalType;
    private Boolean licenseRequired;
    private String managementType;
    private Integer numberOfPeople;
    private String serviceType;
    private String companyName;
    private String description;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String website;
    private BigDecimal maxPrice;
    private String priceSpecification;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}