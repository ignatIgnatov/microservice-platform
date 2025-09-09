package com.platform.ads.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.ads.dto.enums.AdType;
import com.platform.ads.dto.enums.MainBoatCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoatAdRequest {

    @NotNull(message = "Category is required")
    private MainBoatCategory category;

    @NotNull(message = "Ad type is required")
    private AdType adType;

    @NotNull(message = "Price info is required")
    private PriceInfo price;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    private String userEmail;

    @Size(max = 100, message = "Contact person name cannot exceed 100 characters")
    private String contactPersonName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String contactPhone;

    private List<Long> imagesToDelete;

    private BoatSpecificationDto boatSpec;
    private JetSkiSpecificationDto jetSkiSpec;
    private TrailerSpecificationDto trailerSpec;
    private EngineSpecificationDto engineSpec;
    private MarineElectronicsSpecificationDto marineElectronicsSpec;
    private FishingSpecificationDto fishingSpec;
    private PartsSpecificationDto partsSpec;
    private ServicesSpecificationDto servicesSpec;

    private WaterSportsSpecificationDto waterSportsSpec;
    private MarineAccessoriesSpecificationDto marineAccessoriesSpec;
    private RentalsSpecificationDto rentalsSpec;

    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;

//    @Size(min = 5, max = 30, message = "Title must be between 5 and 30 characters")
//    private String title;
//
//    @Size(min = 20, max = 210, message = "Quick description must be between 20 and 210 characters")
//    private String quickDescription;
}