package com.platform.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllBrandsResponse {
    private Map<String, BrandsByCategoryResponse> brandsByCategory;
    private Integer totalBrands;
    private List<String> categories;
}
