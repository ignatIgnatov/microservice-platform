package com.platform.ads.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidatedImageData {
    private String originalFileName;
    private String contentType;
    private byte[] bytes;
    private int width;
    private int height;
}
