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
@Table("services_specifications")
public class ServicesSpecification {
    @Id
    private Long id;
    private Long adId;
    private String serviceType;
    private String companyName;
    private Boolean isAuthorizedService;
    private Boolean isOfficialRepresentative;
    private String description;
    private String contactPhone;
    private String contactPhone2;
    private String contactEmail;
    private String address;
    private String website;
    private String supportedBrands; // comma-separated values
    private String supportedMaterials; // comma-separated enum values
}