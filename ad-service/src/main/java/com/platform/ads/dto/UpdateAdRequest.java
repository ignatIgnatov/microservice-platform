package com.platform.ads.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAdRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;

    @Size(min = 20, max = 210, message = "Quick description must be between 20 and 210 characters")
    private String quickDescription;

    private PriceInfo price;

    @NotBlank(message = "Location is required")
    private String location;
}