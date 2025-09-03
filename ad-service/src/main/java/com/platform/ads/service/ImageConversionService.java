package com.platform.ads.service;

import com.platform.ads.exception.InvalidFieldValueException;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.imaging.Imaging;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class ImageConversionService {

    // WebP quality settings
    private static final float WEBP_QUALITY = 0.85f;
    private static final int MAX_WIDTH = 1920;
    private static final int MAX_HEIGHT = 1080;

    // Supported formats - както искате
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
            "image/heic",
            "image/heif"
    );

    @PostConstruct
    public void initialize() {
        logImageIOCapabilities();
    }

    public Mono<ConvertedImageData> convertToWebP(byte[] originalBytes, String originalFileName, String contentType) {
        return Mono.fromCallable(() -> {
            try {
                long startTime = System.currentTimeMillis();
                log.debug("Starting image conversion: '{}', Type: '{}', Size: {} bytes",
                        originalFileName, contentType, originalBytes.length);

                BufferedImage originalImage = loadImage(originalBytes, contentType);

                if (originalImage == null) {
                    throw new InvalidFieldValueException("image",
                            "Cannot read image file: " + originalFileName);
                }

                // Get original dimensions
                int originalWidth = originalImage.getWidth();
                int originalHeight = originalImage.getHeight();

                // Resize if needed
                BufferedImage processedImage = resizeImageIfNeeded(originalImage);

                // Convert to WebP (с автоматичен fallback)
                byte[] webpBytes = encodeAsWebP(processedImage);

                long duration = System.currentTimeMillis() - startTime;
                double compressionRatio = (1.0 - (double) webpBytes.length / originalBytes.length) * 100;

                log.info("Image conversion successful: '{}', Original: {}x{} ({}KB), WebP: {}x{} ({}KB), Compression: {:.1f}%, Duration: {}ms",
                        originalFileName,
                        originalWidth, originalHeight, originalBytes.length / 1024,
                        processedImage.getWidth(), processedImage.getHeight(), webpBytes.length / 1024,
                        compressionRatio, duration);

                return ConvertedImageData.builder()
                        .convertedBytes(webpBytes)
                        .width(processedImage.getWidth())
                        .height(processedImage.getHeight())
                        .originalSize(originalBytes.length)
                        .convertedSize(webpBytes.length)
                        .compressionRatio(compressionRatio)
                        .processingTimeMs(duration)
                        .build();

            } catch (Exception e) {
                log.error("Image conversion failed: '{}', Error: {}",
                        originalFileName, e.getMessage(), e);
                throw new InvalidFieldValueException("image",
                        "Failed to process image '" + originalFileName + "': " + e.getMessage());
            }
        });
    }

    private BufferedImage loadImage(byte[] imageBytes, String contentType) throws Exception {
        BufferedImage image = null;

        try {
            if ("image/heic".equals(contentType) || "image/heif".equals(contentType)) {
                // Handle HEIC/HEIF files
                image = Imaging.getBufferedImage(imageBytes);
                log.debug("HEIC image loaded using Apache Commons Imaging");
            } else {
                // Standard formats
                image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                log.debug("Standard image loaded using ImageIO");
            }
        } catch (Exception e) {
            log.warn("Primary image loader failed, trying fallback: {}", e.getMessage());

            // Fallback with Thumbnailator
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Thumbnails.of(new ByteArrayInputStream(imageBytes))
                        .scale(1.0)
                        .outputFormat("png")
                        .toOutputStream(outputStream);

                image = ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));
                log.debug("Image loaded with Thumbnailator fallback");
            } catch (Exception fallbackException) {
                log.error("All image loaders failed: Primary: {}, Fallback: {}",
                        e.getMessage(), fallbackException.getMessage());
                throw new Exception("Unable to load image with any available decoder");
            }
        }

        return image;
    }

    private BufferedImage resizeImageIfNeeded(BufferedImage originalImage) throws Exception {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return originalImage;
        }

        double widthRatio = (double) MAX_WIDTH / width;
        double heightRatio = (double) MAX_HEIGHT / height;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);

        log.debug("Resizing image: {}x{} -> {}x{}", width, height, newWidth, newHeight);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
                .size(newWidth, newHeight)
                .outputQuality(1.0f)
                .outputFormat("png")
                .toOutputStream(outputStream);

        return ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    private byte[] encodeAsWebP(BufferedImage image) throws Exception {
        try {
            // Първо опитваме с Thumbnailator за WebP (по-надежден)
            return encodeWithThumbnailatorWebP(image);
        } catch (Exception thumbnailatorError) {
            log.warn("Thumbnailator WebP failed: {}, trying ImageIO WebP", thumbnailatorError.getMessage());

            try {
                // Fallback към ImageIO WebP
                return encodeWithImageIOWebP(image);
            } catch (Exception imageIOError) {
                log.warn("ImageIO WebP also failed: {}, using high-quality JPEG", imageIOError.getMessage());

                // Final fallback - висококачествен JPEG
                return encodeAsJpeg(image, 0.90f);
            }
        }
    }

    private byte[] encodeWithThumbnailatorWebP(BufferedImage image) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(image)
                .scale(1.0)
                .outputFormat("webp")
                .outputQuality(WEBP_QUALITY)
                .toOutputStream(outputStream);

        byte[] result = outputStream.toByteArray();

        if (result.length == 0) {
            throw new Exception("Thumbnailator WebP produced empty output");
        }

        return result;
    }

    private byte[] encodeWithImageIOWebP(BufferedImage image) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");

        if (!writers.hasNext()) {
            throw new Exception("No WebP writers available");
        }

        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        try {
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(WEBP_QUALITY);
            }

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(image, null, null), param);
            }

            byte[] result = outputStream.toByteArray();
            if (result.length == 0) {
                throw new Exception("ImageIO WebP produced empty output");
            }

            return result;
        } finally {
            writer.dispose();
        }
    }

    private byte[] encodeAsJpeg(BufferedImage image, float quality) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedImage rgbImage = ensureRGBColorspace(image);

        Thumbnails.of(rgbImage)
                .scale(1.0)
                .outputFormat("jpg")
                .outputQuality(quality)
                .toOutputStream(outputStream);

        byte[] result = outputStream.toByteArray();

        if (result.length == 0) {
            throw new Exception("JPEG encoding produced empty output");
        }

        return result;
    }

    private BufferedImage ensureRGBColorspace(BufferedImage original) {
        if (original.getType() == BufferedImage.TYPE_INT_RGB) {
            return original;
        }

        BufferedImage rgbImage = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2d = rgbImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, original.getWidth(), original.getHeight());
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();

        return rgbImage;
    }

    private void logImageIOCapabilities() {
        try {
            log.info("=== IMAGE IO CAPABILITIES ===");
            String[] formats = ImageIO.getWriterFormatNames();
            log.info("Available output formats: {}", Arrays.toString(formats));

            // Check WebP specifically
            Iterator<ImageWriter> webpWriters = ImageIO.getImageWritersByFormatName("webp");
            if (webpWriters.hasNext()) {
                log.info("WebP support: AVAILABLE");
            } else {
                log.info("WebP support: NOT AVAILABLE (will use fallback)");
            }

        } catch (Exception e) {
            log.warn("Failed to check ImageIO capabilities: {}", e.getMessage());
        }
    }

    public boolean isFormatSupported(String contentType) {
        return ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase());
    }

    @Builder
    @Data
    public static class ConvertedImageData {
        private byte[] convertedBytes;
        private int width;
        private int height;
        private long originalSize;
        private long convertedSize;
        private double compressionRatio;
        private long processingTimeMs;
    }
}