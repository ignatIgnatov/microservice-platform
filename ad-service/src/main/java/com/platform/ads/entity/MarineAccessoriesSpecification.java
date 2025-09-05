package com.platform.ads.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("marine_accessories_specifications")
public class MarineAccessoriesSpecification {
    @Id
    private Long id;
    private Long adId;
    private String accessoryType;
    private String brand;
    private String condition;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}