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
@Table("ad_images")
public class AdImage {
    @Id
    private Long id;
    private Long adId;
    private String fileName;
    private String originalFileName;
    private String s3Key;
    private String s3Url;
    private String contentType;
    private Long fileSize;
    private Integer displayOrder;
    private Integer width;
    private Integer height;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    private Boolean active;
}