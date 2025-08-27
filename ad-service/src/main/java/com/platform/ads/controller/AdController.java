package com.platform.ads.controller;

import com.platform.ads.entity.Ad;
import com.platform.ads.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ads")
@CrossOrigin(origins = "*")
public class AdController {

    @Autowired
    private AdService adService;

    @GetMapping
    public ResponseEntity<Page<Ad>> getAllAds(Pageable pageable) {
        Page<Ad> ads = adService.getAllAds(pageable);
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ad> getAdById(@PathVariable Long id) {
        Ad ad = adService.getAdById(id);
        return ResponseEntity.ok(ad);
    }

    @PostMapping
    public ResponseEntity<Ad> createAd(@Valid @RequestBody Ad ad, Authentication authentication) {
        String userId = authentication.getName();
        ad.setUserId(userId);
        Ad createdAd = adService.createAd(ad);
        return ResponseEntity.ok(createdAd);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ad> updateAd(@PathVariable Long id, @Valid @RequestBody Ad ad,
                                       Authentication authentication) {
        String userId = authentication.getName();
        Ad updatedAd = adService.updateAd(id, ad, userId);
        return ResponseEntity.ok(updatedAd);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id, Authentication authentication) {
        String userId = authentication.getName();
        adService.deleteAd(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/my-ads")
    public ResponseEntity<Page<Ad>> getUserAds(Authentication authentication, Pageable pageable) {
        String userId = authentication.getName();
        Page<Ad> ads = adService.getAdsByUserId(userId, pageable);
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Ad>> getAdsByCategory(@PathVariable String category, Pageable pageable) {
        Page<Ad> ads = adService.getAdsByCategory(category, pageable);
        return ResponseEntity.ok(ads);
    }
}