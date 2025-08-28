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
public class AdSearchRequest {

    private String query; // Search in title and description

    private String category;

    private String location;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String userEmail; // Search ads by specific user

    private Boolean active; // Filter by active status

    private String sortBy; // "createdAt", "price", "title"

    private String sortDirection; // "asc", "desc"

    private Integer page;

    private Integer size;
}