package com.platform.ads.dto;

import com.platform.ads.dto.enums.AdType;
import com.platform.ads.dto.enums.BoatCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoatAdResponse {

    private Long id;
    private String title;
    private String description;
    private String quickDescription;
    private BoatCategory category;
    private PriceInfo price;
    private String location;
    private AdType adType;
    private String userEmail;
    private String userId;
    private String userFirstName;
    private String userLastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;
    private Integer viewsCount;
    private Boolean featured;

    // Category-specific specifications
    private BoatSpecificationResponse boatSpec;
    private JetSkiSpecificationResponse jetSkiSpec;
    private TrailerSpecificationResponse trailerSpec;
    private EngineSpecificationResponse engineSpec;
    private MarineElectronicsSpecificationResponse marineElectronicsSpec;
    private FishingSpecificationResponse fishingSpec;
    private PartsSpecificationResponse partsSpec;
    private ServicesSpecificationResponse servicesSpec;

    // Computed fields
    private String userFullName;
    private String formattedPrice;
    private String categoryDisplayName;

    public String getUserFullName() {
        if (userFirstName != null && userLastName != null) {
            return userFirstName + " " + userLastName;
        } else if (userFirstName != null) {
            return userFirstName;
        } else if (userLastName != null) {
            return userLastName;
        }
        return userEmail;
    }

    public String getFormattedPrice() {
        if (price == null) return "";

        if (price.getType() != PriceInfo.PriceType.FIXED_PRICE) {
            return price.getType().getDisplayName();
        }

        String priceStr = price.getAmount() + " лв";
        if (price.getIncludingVat() != null) {
            priceStr += price.getIncludingVat() ? " с ДДС" : " без ДДС";
        }
        return priceStr;
    }

    public String getCategoryDisplayName() {
        return category != null ? category.getDisplayName() : "";
    }
}
