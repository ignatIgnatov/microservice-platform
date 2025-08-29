package com.platform.ads.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("fishing_specifications")
public class FishingSpecification {
    @Id
    private Long id;
    private Long adId;
    private String fishingType;
    private String brand;
    private String fishingTechnique;
    private String targetFish;
    private String condition;
}