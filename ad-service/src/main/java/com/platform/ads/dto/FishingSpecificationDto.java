package com.platform.ads.dto;

import com.platform.ads.dto.enums.ItemCondition;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FishingSpecificationDto {
    private FishingType fishingType;
    private String brand;
    private FishingTechnique fishingTechnique;
    private TargetFish targetFish;
    private ItemCondition condition;

    public enum FishingType {
        RODS, REELS, LINES, HOOKS, LURES, BAIT_FEED_ADDITIVES,
        BAIT_BOATS, HARPOONS, ROD_STANDS, FISHING_CLOTHING, OTHER
    }

    public enum FishingTechnique {
        SPINNING, JIGGING, BAITCASTING, BIG_GAME, TROLLING,
        FEEDER, CARP_AND_BOTTOM, FLOAT_AND_BOMBARDA
    }

    public enum TargetFish {
        CARP_AMUR, WHITE_FISH_KOSTUR, FRESHWATER_PEACEFUL_FISH, PIKE, CATFISH,
        TROUT_CHUB_RIVER_MULLET, NEEDLEFISH, BLUEFISH, SQUID, BREAM,
        MARINE_MULLET_PLATARINA_ILARIA, DORADO_MAHIMAHI, HORSE_MACKEREL,
        TUNA, GROUPER_MEROU, BLACKHEAD_KARAGIOZ_DANUBE_MACKEREL,
        AMBERJACK_RICOLA_MAYATICO, BOTTOM_MARINE_SPECIES, BASS_LAVRAKI_BONITO,
        BARRACUDA, SEA_BREAM, OTHER
    }
}