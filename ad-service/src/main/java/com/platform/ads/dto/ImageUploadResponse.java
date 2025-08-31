package com.platform.ads.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ImageUploadResponse {
    private Long id;
    private String fileName;
    private String originalFileName;
    private String url;
    private String contentType;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private Integer displayOrder;
    private LocalDateTime uploadedAt;
}
