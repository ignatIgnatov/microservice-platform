package com.platform.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandsByCategoryResponse {
    private String category;
    private String categoryDisplayName;
    private Integer totalCount;
    private List<BrandResponse> brands;
}
