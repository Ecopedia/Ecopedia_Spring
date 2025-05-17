package com.ecopedia.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HomeResponseDto {

    private PlantStatus plantStatus;
    private DonationStatus donationStatus;
    private List<RecentCreature> recentCollected;

    @Getter
    @AllArgsConstructor
    public static class PlantStatus {
        private int savedCreatureCount;
        private int nextTreeDonationProgress;
        private int availableDonationTreeCount;
    }

    @Getter
    @AllArgsConstructor
    public static class DonationStatus {
        private int donatedTrees;
        private int donationWon;
    }

    @Getter
    @AllArgsConstructor
    public static class RecentCreature {
        private long id;
        private String name;
        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class DonationResult {
        private int availableDonationTreeCount;
        private int donatedTrees;
        private int donationWon;
    }



}
