package com.ecopedia.server.dto;

import com.ecopedia.server.domain.enums.CreatureCategory;
import lombok.Builder;
import lombok.Data;

public class ResponseDto {

    @Builder
    @Data
    public static class ImgDto {
        String creatureName;
        // 주소 넣어야함
        String category;
        String explain;
    }

    @Data
    @Builder
    public static class CreatureDetailResponseDto {
        private Long creatureIdx;
        private String creatureName;
        private String creatureExplain;
        private Double latitude;
        private Double longitude;
        private CreatureCategory category;
        private String imageUrl;
    }
}
