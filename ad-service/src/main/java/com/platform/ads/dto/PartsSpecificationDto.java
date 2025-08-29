package com.platform.ads.dto;

import com.platform.ads.dto.enums.ItemCondition;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartsSpecificationDto {
    private PartType partType;
    private ItemCondition condition;

    public enum PartType {
        HOSES, STANDS, PROPELLERS, IMPELLERS, TRIM_TABS, GASKETS,
        ENGINE_MOUNTS, REPAIR_KITS, TRAILER_PARTS, FENDERS_AND_BUOYS,
        COVERS, CONSOLES, OTHER
    }
}