package com.ecopedia.server.web.dto.location;

import lombok.Data;

@Data
public class LocationReturnDto {
    private String sido;    // 시도 (region_1depth_name)
    private String sigungu; // 시군구 (region_2depth_name)
    private String dong;    // 동 (region_3depth_name)
}
