package com.platform.ads.service;

import com.platform.ads.entity.Ad;
import com.platform.ads.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdService {

    @Autowired
    private AdRepository adRepository;

    public Page<Ad> getAllAds(Pageable pageable) {
        return adRepository.findByStatus(Ad.AdStatus.ACTIVE, pageable);
    }

    public Ad getAdById(Long id) {
        return adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));
    }

    public Ad createAd(Ad ad) {
        ad.setCreatedAt(LocalDateTime.now());
        ad.setStatus(Ad.AdStatus.ACTIVE);
        return adRepository.save(ad);
    }

    public Ad updateAd(Long id, Ad adUpdate, String userId) {
        Ad existingAd = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        if (!existingAd.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this ad");
        }

        existingAd.setTitle(adUpdate.getTitle());
        existingAd.setDescription(adUpdate.getDescription());
        existingAd.setPrice(adUpdate.getPrice());
        existingAd.setCategory(adUpdate.getCategory());
        existingAd.setImageUrl(adUpdate.getImageUrl());
        existingAd.setUpdatedAt(LocalDateTime.now());

        return adRepository.save(existingAd);
    }

    public void deleteAd(Long id, String userId) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        if (!ad.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this ad");
        }

        adRepository.delete(ad);
    }

    public Page<Ad> getAdsByUserId(String userId, Pageable pageable) {
        return adRepository.findByUserId(userId, pageable);
    }

    public Page<Ad> getAdsByCategory(String category, Pageable pageable) {
        return adRepository.findByCategory(category, pageable);
    }
}