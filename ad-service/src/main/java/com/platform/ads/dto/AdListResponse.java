package com.platform.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdListResponse {

    private java.util.List<AdResponse> ads;

    private Long totalElements;

    private Integer totalPages;

    private Integer currentPage;

    private Integer pageSize;

    private Boolean hasNext;

    private Boolean hasPrevious;
}
