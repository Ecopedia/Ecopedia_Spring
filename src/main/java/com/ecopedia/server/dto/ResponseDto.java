package com.ecopedia.server.dto;

import com.ecopedia.server.domain.Location;
import com.ecopedia.server.domain.enums.CreatureCategory;
import lombok.Builder;
import lombok.Data;

public class ResponseDto {

    @Builder
    @Data
    public static class ImgDto {
        String creatureName;
        Location location;
        String category;
        String explain;
    }

    @Data
    @Builder
    public static class CreatureDetailResponseDto {

        private Long creatureIdx;
        private String creatureName;
        private String creatureExplain;
        private String category;

        private String si;
        private String gu;
        private String dong;

        private String imageUrl;
    }


    @Data
    @Builder
    public static class CreatureSummaryDto {
        private Long idx;
        private String creatureName;
        private String creatureExplain;
        private CreatureCategory category;
        private String imageUrl;
        private Location location;
    }
}
