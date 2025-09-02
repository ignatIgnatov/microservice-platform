package com.platform.ads.controller;

import com.platform.ads.dto.ImageUploadResponse;
import com.platform.ads.dto.ImageReorderRequest;
import com.platform.ads.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/ads/images")
@RequiredArgsConstructor
@Tag(name = "Ad Images", description = "Upload and manage advertisement images")
@SecurityRequirement(name = "bearerAuth")
public class ImageController {

    private final ImageService imageService;

    // ===========================
    // UPLOAD ENDPOINTS
    // ===========================

    @PostMapping("/{adId}/upload")
    @Operation(
            summary = "Upload single image to advertisement",
            description = "Uploads a single image to the specified advertisement. Supported formats: JPEG, PNG, WEBP. Max size: 5MB."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format or size"),
            @ApiResponse(responseCode = "403", description = "User does not own this advertisement"),
            @ApiResponse(responseCode = "404", description = "Advertisement not found")
    })
    public Mono<ResponseEntity<ImageUploadResponse>> uploadImage(
            @Parameter(description = "Advertisement ID", required = true)
            @PathVariable @NotNull @Min(1) Long adId,

            @Parameter(description = "Image file to upload", required = true)
            @RequestPart("image") Mono<FilePart> imageFile,

            Authentication authentication) {

        String userId = authentication.getName();
        log.info("=== UPLOAD IMAGE REQUEST === UserId: {}, AdID: {} ===", userId, adId);

        return imageFile
                .flatMap(filePart -> imageService.uploadImage(userId, adId, filePart))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.warn("=== UPLOAD IMAGE BAD REQUEST === UserId: {}, AdID: {}, Error: {} ===",
                            userId, adId, e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(error -> {
                    log.error("=== UPLOAD IMAGE ERROR === UserId: {}, AdID: {}, Error: {} ===",
                            userId, adId, error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/{adId}/upload-multiple")
    @Operation(
            summary = "Upload multiple images to advertisement",
            description = "Uploads multiple images to the specified advertisement. Failed uploads are skipped."
    )
    public Flux<ImageUploadResponse> uploadMultipleImages(
            @PathVariable @NotNull @Min(1) Long adId,
            @RequestPart("images") Flux<FilePart> imageFiles,
            Authentication authentication) {

        String userId = authentication.getName();
        log.info("=== UPLOAD MULTIPLE IMAGES REQUEST === UserId: {}, AdID: {} ===", userId, adId);

        return imageService.uploadMultipleImages(userId, adId, imageFiles);
    }

    // ===========================
    // MANAGEMENT ENDPOINTS
    // ===========================

    @GetMapping("/{adId}")
    @Operation(
            summary = "Get all images for advertisement",
            description = "Retrieves all images for the specified advertisement, ordered by display order"
    )
    public Flux<ImageUploadResponse> getAdImages(
            @Parameter(description = "Advertisement ID", required = true)
            @PathVariable @NotNull @Min(1) Long adId) {

        log.info("=== GET AD IMAGES REQUEST === AdID: {} ===", adId);
        return imageService.getAdImages(adId);
    }

    @GetMapping("/{adId}/primary")
    @Operation(
            summary = "Get primary image for advertisement",
            description = "Retrieves the primary (first) image for the specified advertisement"
    )
    public Mono<ResponseEntity<ImageUploadResponse>> getPrimaryImage(
            @PathVariable @NotNull @Min(1) Long adId) {

        log.info("=== GET PRIMARY IMAGE REQUEST === AdID: {} ===", adId);

        return imageService.getPrimaryImage(adId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{imageId}")
    @Operation(
            summary = "Delete image",
            description = "Permanently deletes an image. Only the ad owner can delete images."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User does not own this image"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public Mono<ResponseEntity<Void>> deleteImage(
            @Parameter(description = "Image ID to delete", required = true)
            @PathVariable @NotNull @Min(1) Long imageId,

            Authentication authentication) {

        String userId = authentication.getName();
        log.info("=== DELETE IMAGE REQUEST === UserId: {}, ImageID: {} ===", userId, imageId);

        return imageService.deleteImage(userId, imageId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.warn("=== DELETE IMAGE FORBIDDEN === UserId: {}, ImageID: {} ===", userId, imageId);
                    return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                });
    }

    @PutMapping("/{adId}/reorder")
    @Operation(
            summary = "Reorder advertisement images",
            description = "Changes the display order of images for an advertisement. First image becomes primary."
    )
    public Mono<ResponseEntity<Void>> reorderImages(
            @PathVariable @NotNull @Min(1) Long adId,
            @Valid @RequestBody ImageReorderRequest request,
            Authentication authentication) {

        String userId = authentication.getName();
        log.info("=== REORDER IMAGES REQUEST === UserId: {}, AdID: {}, NewOrder: {} ===",
                userId, adId, request.getImageIds());

        return imageService.reorderImages(userId, adId, request.getImageIds())
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(IllegalArgumentException.class, e -> {
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }
}