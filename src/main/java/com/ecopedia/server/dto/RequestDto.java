package com.ecopedia.server.dto;

import lombok.Builder;
import lombok.Data;

public class RequestDto {

    @Builder
    @Data
    public static class CreatureSaveRequestDto {

        // 생물 정보
        private String creatureName;
        private String creatureExplain;
        private String category;

        private Double latitude;
        private Double longitude;

        // 이미지 정보
        private String imageUrl;
        private String imageKey;
    }
}
