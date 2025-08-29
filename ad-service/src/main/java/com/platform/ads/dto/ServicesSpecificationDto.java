package com.platform.ads.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ServicesSpecificationDto {
    private ServiceType serviceType;
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
    private List<MaterialType> supportedMaterials;

    public enum ServiceType {
        BOAT_REPAIR, ENGINE_REPAIR
    }

    public enum MaterialType {
        FIBERGLASS, WOOD, ALUMINUM, PVC, HYPALON, RUBBER
    }
}