package com.platform.ads.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImageReorderRequest {
    private List<Long> imageIds;
}
