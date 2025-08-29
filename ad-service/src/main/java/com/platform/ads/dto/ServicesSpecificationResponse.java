package com.platform.ads.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ServicesSpecificationResponse {
    private ServicesSpecificationDto.ServiceType serviceType;
    private String companyName;
    private Boolean isAuthorizedService;
    private Boolean isOfficialRepresentative;
    private String description;
    private String contactPhone;
    private String contactPhone2;
    private String contactEmail;
    private String address;
    private String website;
    private List<String> supportedBrands;
    private List<ServicesSpecificationDto.MaterialType> supportedMaterials;
}
